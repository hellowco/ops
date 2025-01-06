package kr.co.proten.llmops.api.knowledge.dto;

import lombok.Builder;

@Builder
public record KnowledgeDTO(
        String id,
        String modelName,
        String knowledgeName,
        String description
) {}
