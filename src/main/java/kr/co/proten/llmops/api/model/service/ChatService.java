package kr.co.proten.llmops.api.model.service;

import kr.co.proten.llmops.api.model.dto.request.ModelRequest;
import kr.co.proten.llmops.api.model.dto.response.ChatResponse;
import reactor.core.publisher.Flux;

public interface ChatService {
    String getServiceType(); // 서비스 타입 반환
    Flux<ChatResponse> processChat(ModelRequest request);
}