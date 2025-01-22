package kr.co.proten.llmops.api.model.service.impl;

import kr.co.proten.llmops.api.model.dto.request.ModelRequest;
import kr.co.proten.llmops.api.model.dto.response.ChatResponse;
import kr.co.proten.llmops.api.model.service.ChatService;
import kr.co.proten.llmops.core.exception.ChatProcessingException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.ai.chat.messages.Message;
import org.springframework.ai.chat.messages.UserMessage;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.util.retry.Retry;

import java.time.Duration;
import java.util.List;
import java.util.concurrent.TimeoutException;

public abstract class AbstractChatService implements ChatService {

    private static final Logger log = LoggerFactory.getLogger(AbstractChatService.class);
    private static final Duration TIMEOUT_DURATION = Duration.ofSeconds(10);

    @Override
    public Flux<ChatResponse> processChat(ModelRequest request) {
        return Flux.defer(() -> {
            validateRequest(request); // 유효성 검사 로직
            try {
                ChatModel chatModel = createChatModel(request);
                Prompt prompt = createPrompt(request);

                log.info("Starting model request: Model={}, Prompt={}", request.model(), request.query());

                return chatModel.stream(prompt)
                        .timeout(TIMEOUT_DURATION)
                        .map(response -> new ChatResponse(response.getResult().getOutput().getContent(), "IN_PROGRESS"))
                        .doOnComplete(() -> log.info("Chat processing completed successfully"))
                        .doOnError(this::handleError)
                        .retryWhen(Retry.backoff(3, Duration.ofSeconds(1))
                                .filter(this::isRetryableError)
                                .doBeforeRetry(signal -> log.warn("Retrying after error: {}, attempt: {}",
                                        signal.failure().getMessage(), signal.totalRetries() + 1)))
                        .concatWith(createCompletionResponse())
                        .onErrorResume(this::handleErrorResponse);
            } catch (Exception e) {
                log.error("Failed to initialize chat model: {}", e.getMessage(), e);
                return Flux.error(new ChatProcessingException("Failed to initialize chat model"));
            }
        });
    }

    protected abstract ChatModel createChatModel(ModelRequest request);

    private Prompt createPrompt(ModelRequest request) {
        List<Message> userMessage = List.of(
                new UserMessage(request.instruction()),
                new UserMessage("<context>"),
                new UserMessage(request.documents().toString()),
                new UserMessage("</context>"),
                new UserMessage(request.query())
        );
        return new Prompt(userMessage);
    }

    private void handleError(Throwable e) {
        log.error("Error during chat processing: {}", e.getMessage());
    }

    private boolean isRetryableError(Throwable error) {
        return error instanceof TimeoutException || error instanceof WebClientResponseException;
    }

    private Mono<ChatResponse> createCompletionResponse() {
        return Mono.fromCallable(() -> {
            boolean isCancelled = Thread.currentThread().isInterrupted();
            String status = isCancelled ? "CANCEL" : "COMPLETE";
            String message = isCancelled ? "Stream was cancelled." : "Stream completed successfully.";
            log.info(message);
            return new ChatResponse(message, status);
        });
    }

    private Mono<ChatResponse> handleErrorResponse(Throwable e) {
        log.error("Sending error response to client: {}", e.getMessage());
        return Mono.just(new ChatResponse(e.getMessage(), "ERROR"));
    }

    /**
     * 공통 유효성 검사 로직
     */
    private void validateRequest(ModelRequest request) {
        if (request == null) {
            throw new IllegalArgumentException("Request cannot be null");
        }
        if (request.model() == null || request.model().trim().isEmpty()) {
            throw new IllegalArgumentException("Model name cannot be null or empty");
        }
        if (request.query() == null || request.query().trim().isEmpty()) {
            throw new IllegalArgumentException("User prompt cannot be null or empty");
        }
    }
}
