package kr.co.proten.llmops.api.model.service;

import kr.co.proten.llmops.api.model.dto.request.ModelRequest;
import kr.co.proten.llmops.api.model.dto.response.ChatResponse;
import reactor.core.publisher.Flux;

public interface ChatService {
    String getServiceType(); // 서비스 타입 반환
    Flux<ChatResponse> processChat(ModelRequest request);

    default void validateRequest(ModelRequest request) {
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