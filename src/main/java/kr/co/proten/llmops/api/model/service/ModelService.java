package kr.co.proten.llmops.api.model.service;

import kr.co.proten.llmops.api.model.dto.request.ModelRequest;
import kr.co.proten.llmops.api.model.dto.response.ChatResponse;
import reactor.core.publisher.Flux;

public interface ModelService {
    Flux<ChatResponse> streamChat(ModelRequest modelRequest);
}
