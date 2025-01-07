package kr.co.proten.llmops.api.app.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;

public record AppSearchDTO (
        @NotBlank
        @Schema(description = "워크스페이스 ID", example = "8ee589ef-c7bb-4f2a-a773-630abd0de8c7")
        String workspace_id,

        @NotBlank
        @Schema(description = "워크스페이스 이름", example = "다락방")
        String name
) {}
