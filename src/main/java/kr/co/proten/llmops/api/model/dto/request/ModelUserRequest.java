package kr.co.proten.llmops.api.model.dto.request;

public record ModelUserRequest(
    String provider,
    String name,
    String modelType
){
    public ModelUserRequest {
        provider = provider != null ? provider.toUpperCase() : null;
        modelType = modelType != null ? modelType.toUpperCase() : null;
    }
}
