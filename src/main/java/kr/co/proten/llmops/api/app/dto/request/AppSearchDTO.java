package kr.co.proten.llmops.api.app.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;

public record AppSearchDTO (
        @Schema(description = "워크스페이스 UUID", example = "c23e6d8b-bdeb-4bf7-a0c1-4f578fb3561d-1735876389")
        String workspace_id,

        @Schema(description = "워크스페이스 이름", example = "다락방")
        String name
) {}
