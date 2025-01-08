package kr.co.proten.llmops.api.app.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.annotation.Nullable;
import jakarta.validation.constraints.NotBlank;

public record AppUpdateDTO(
        @NotBlank
        @Schema(description = "앱 UUID", example = "ab88840c-9b36-4ed5-abfb-dc616544f14b-1736137815")
        String app_id,

        @NotBlank
        @Schema(description = "앱 이름", example = "ProRAG")
        String name,

        @Nullable
        @Schema(description = "앱 설명", example = "기본적인 Naive RAG")
        String description
) {
}
