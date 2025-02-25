package kr.co.proten.llmops.api.model.dto.response;

import kr.co.proten.llmops.api.model.entity.Provider;
import lombok.Builder;

@Builder
public record ProviderResponseDTO (
    String name,
    String description,
    String icon
){
    public static ProviderResponseDTO entityToResponse(Provider provider) {
        return ProviderResponseDTO.builder()
                .name(provider.getName())
                .description(provider.getDescription())
                .icon(provider.getIcon())
                .build();
    }
}
