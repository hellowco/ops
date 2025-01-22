package kr.co.proten.llmops.api.app.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;

public record AppSearchDTO (
        @NotBlank
        @Schema(description = "워크스페이스 ID", example = "8ee589ef-c7bb-4f2a-a773-630abd0de8c7")
        String workspace_id,

        @NotBlank
        @Schema(description = "앱 이름", example = "ProRAG")
        String name,

        @NotBlank
        @Schema(description = "페이지 번호", example = "1")
        int page,

        @NotBlank
        @Schema(description = "페이지당 갯수", example = "10")
        int size,

        @NotBlank
        @Schema(description = "정렬 기준(createdAt/name)", example = "name")
        String sort_field,

        @NotBlank
        @Schema(description = "정렬 순서(asc/desc)", example = "asc")
        String sort_by
) {}
