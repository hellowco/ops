package kr.co.proten.llmops.api.knowledge.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;

@Builder
public record KnowledgeDTO(

        @Schema(description = "지식 ID", example = "8ee589ef-c7bb-4f2a-a773-630abd0de8c7")
        String id,

        @Schema(description = "지식 임베딩에 사용한 모델명", example = "deepseek-r1:8b")
        String modelName,

        @Schema(description = "지식명", example = "솔루션사업부")
        String knowledgeName,

        @Schema(description = "지식 설명", example = "솔루션사업부 내 공유자료 저장소")
        String description
) {}
