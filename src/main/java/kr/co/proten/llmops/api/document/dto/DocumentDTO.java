package kr.co.proten.llmops.api.document.dto;

import lombok.Builder;

import java.util.List;

@Builder
public record DocumentDTO(
        String id,
        String docId,
        String index,
        boolean isActive,
        String content,
        List<Double> contentVec,
        long page) {
}
