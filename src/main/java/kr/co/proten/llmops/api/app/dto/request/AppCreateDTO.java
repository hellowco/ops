package kr.co.proten.llmops.api.app.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.annotation.Nullable;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record AppCreateDTO(
        @NotBlank
        @Schema(description = "워크스페이스 ID", example = "8ee589ef-c7bb-4f2a-a773-630abd0de8c7")
        String workspace_id,

        @NotNull
        @Size(max=10)
        @Schema(description = "앱 이름", example = "ProRAG")
        String name,

        @Nullable
        @Schema(description = "앱 설명", example = "기본적인 Naive RAG")
        String description
) {
}
