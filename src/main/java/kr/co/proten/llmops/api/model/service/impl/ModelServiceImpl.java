package kr.co.proten.llmops.api.model.service.impl;

import kr.co.proten.llmops.api.model.dto.request.ModelRequest;
import kr.co.proten.llmops.api.model.dto.response.ChatResponse;
import kr.co.proten.llmops.api.model.service.ChatService;
import kr.co.proten.llmops.api.model.service.ModelService;
import kr.co.proten.llmops.api.model.service.factory.ChatFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.http.codec.ServerSentEvent;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;

@Service
@RequiredArgsConstructor
public class ModelServiceImpl implements ModelService {

    private final ChatFactory chatFactory;

    @Override
    public Flux<ChatResponse> streamChat(ModelRequest modelRequest) {
        ChatService chatService = chatFactory.getChatService(modelRequest.provider())
                .orElseThrow(() -> new UnsupportedOperationException("존재하지 않는 제공자입니다."));

        return chatService.processChat(modelRequest);
    }
}
