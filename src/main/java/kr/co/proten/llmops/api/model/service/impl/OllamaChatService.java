package kr.co.proten.llmops.api.model.service.impl;

import kr.co.proten.llmops.api.model.dto.request.ModelRequest;
import kr.co.proten.llmops.api.model.dto.response.ChatResponse;
import kr.co.proten.llmops.api.model.service.ChatService;
import kr.co.proten.llmops.core.config.ai.OllamaConfig;
import kr.co.proten.llmops.core.exception.ChatProcessingException;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.ai.chat.messages.Message;
import org.springframework.ai.chat.messages.UserMessage;
import org.springframework.ai.chat.model.MessageAggregator;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.ai.ollama.OllamaChatModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.codec.ServerSentEvent;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClientException;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;
import reactor.util.retry.Retry;

import java.time.Duration;
import java.util.List;
import java.util.concurrent.TimeoutException;

@Service
@RequiredArgsConstructor
public class OllamaChatService implements ChatService {

    private static final Logger log = LoggerFactory.getLogger(OllamaChatService.class);
    private static final Duration TIMEOUT_DURATION = Duration.ofSeconds(10);

    private final OllamaConfig ollamaConfig;

    @Override
    public Flux<ChatResponse> processChat(ModelRequest request) {
        final String host = "192.168.0.28";
        final int port = 11434;

        return Flux.defer(() -> {
                    validateRequest(request);

                    try {
                        OllamaChatModel chatModel = ollamaConfig.createChatModel(
                                host,
                                port,
                                request.model()
                        );

                        List<Message> userMessage = List.of(
                                new UserMessage(request.instruction()),
                                new UserMessage("<context>"),
                                new UserMessage(request.documents().toString()),
                                new UserMessage("</context>"),
                                new UserMessage(request.query())
                        );

                        Prompt prompt = new Prompt(userMessage);

                        log.info("Starting model request: Host={}, Port={}, Model={}, Prompt={}",
                                host, port, request.model(), request.query());

                        return chatModel.stream(prompt)
                                .timeout(TIMEOUT_DURATION)
                                .map(response -> new ChatResponse(response.getResult().getOutput().getContent(), "IN_PROGRESS"))
                                .doOnComplete(() -> log.info("Chat processing completed successfully"))
                                .doOnCancel(() -> log.warn("Chat processing was cancelled by client"))
                                .doOnError(e -> {
                                    log.error("Error during chat processing: {}", e.getMessage());
                                    throw new ChatProcessingException("Error during chat processing");
                                })
                                .onErrorMap(TimeoutException.class, e ->
                                        new ChatProcessingException("Request timed out after " + TIMEOUT_DURATION.getSeconds() + " seconds"))
                                .onErrorMap(WebClientResponseException.class, e ->
                                        new ChatProcessingException("API Response Error: " +  e.getResponseBodyAsString()))
                                .onErrorMap(Exception.class, e ->
                                        new ChatProcessingException("Chat processing failed"))
                                .retryWhen(Retry.backoff(3, Duration.ofSeconds(1))
                                        .filter(error ->
                                                error instanceof TimeoutException ||
                                                error instanceof WebClientResponseException)
                                        .doBeforeRetry(signal -> log.warn("Retrying after error: {}, attempt: {}",
                                                signal.failure().getMessage(), signal.totalRetries() + 1)))
                                .doFinally(signalType -> log.info("Stream ended with signal: {}", signalType.name()))
                                .concatWith(Mono.fromCallable(() -> {
                                    boolean isCancelled = Thread.currentThread().isInterrupted();
                                    String status = isCancelled ? "CANCEL" : "COMPLETE";
                                    String message = isCancelled ? "Stream was cancelled." : "Stream completed successfully.";
                                    log.info(message);
                                    return new ChatResponse(message, status);
                                }))
                                .onErrorResume(e -> {
                                    log.error("Sending error response to client: {}", e.getMessage());
                                    return Mono.just(new ChatResponse(e.getMessage(), "ERROR"));
                                });

                    } catch (Exception e) {
                        log.error("Failed to initialize chat model: {}", e.getMessage(), e);
                        return Flux.error(new ChatProcessingException("Failed to initialize chat model"));
                    }
                })
                .subscribeOn(Schedulers.boundedElastic()); //비동기 방식으로 실행. IO 작업이나 CPU 집약적인 작업이 메인 스레드를 차단하지 않도록 설계.
    }

    @Override
    public String getServiceType() {
        return "ollama";
    }
}