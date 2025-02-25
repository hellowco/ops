package kr.co.proten.llmops.api.model.service;

import kr.co.proten.llmops.api.model.dto.request.ModelListRequest;
import kr.co.proten.llmops.api.model.dto.request.ModelRequest;
import kr.co.proten.llmops.api.model.dto.request.ModelUserRequest;
import kr.co.proten.llmops.api.model.dto.response.ChatResponse;
import reactor.core.publisher.Flux;

import java.util.Map;

public interface ProviderService {
    Flux<ChatResponse> streamChat(ModelRequest modelRequest);

    Map<String, Object> getModelList(ModelListRequest modelListRequest);

    Map<String, Object> getAllModelList(String provider, String modelType);

    Map<String, Object> getModelList(String modelType);

    Map<String, Object> saveModel(ModelUserRequest modelUserRequest);

    Map<String, Object> deleteModel(String modelId);

    Map<String, Object> getProviderList();
}
