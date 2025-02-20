package kr.co.proten.llmops.api.document.dto;

import static kr.co.proten.llmops.core.helpers.UUIDGenerator.generateUUID4Doc;

import io.swagger.v3.oas.annotations.media.Schema;
import kr.co.proten.llmops.api.document.entity.Document;

import lombok.Builder;
import lombok.extern.slf4j.Slf4j;

@Builder
@Slf4j
public record DocumentDTO(

        @Schema(description = "고유 ID", example = "8ee589ef-c7bb-4f2a-a773-630abd0de8c7")
        String id,

        @Schema(description = "문서 ID", example = "8ee589ef-c7bb-4f2a-a773-630abd0de8c7")
        String docId,

        @Schema(description = "청크 ID", example = "1")
        long chunkId,

        @Schema(description = "지식명", example = "솔루션사업부")
        String knowledgeName,

        @Schema(description = "문서 활성 여부", example = "true")
        boolean isActive,

        @Schema(description = "내용", example = "1. 폴더 하나 만들기 2. git clone으로 다운로드 받음")
        String content,

        @Schema(description = "페이지 번호", example = "1")
        long page,

        @Schema(description = "검색 쿼리에 다른 유사도", example = "13")
        double score
    ) {

    public static DocumentDTO fromEntity(Document document) {
        log.debug("score of document: {}", document.getScore());
        return DocumentDTO.builder()
                .id(document.getId())
                .docId(document.getDocId())
                .chunkId(document.getChunkId())
                .knowledgeName(document.getKnowledgeName())
                .isActive(document.isActive())
                .content(document.getContent())
                .page(document.getPage())
                .score(document.getScore())
                .build();
    }
}
