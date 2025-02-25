package kr.co.proten.llmops.api.model.service;

import kr.co.proten.llmops.api.model.dto.request.ModelListRequest;
import kr.co.proten.llmops.api.model.dto.request.ModelRequest;
import kr.co.proten.llmops.api.model.dto.response.ChatResponse;
import reactor.core.publisher.Flux;
import java.util.List;

public interface ModelService {
    String getProviderType(); // 서비스 타입 반환
    Flux<ChatResponse> processChat(ModelRequest request);
    List<String> getEmbedModelList(ModelListRequest modelListRequest);
    List<String> getSearchModelList(ModelListRequest modelListRequest);
    int getEmbeddingDimensions(String name);
}