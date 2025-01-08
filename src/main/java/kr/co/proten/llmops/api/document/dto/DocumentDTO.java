package kr.co.proten.llmops.api.document.dto;

import static kr.co.proten.llmops.core.helpers.UUIDGenerator.generateUUID4Doc;

import kr.co.proten.llmops.api.document.entity.Document;

import lombok.Builder;
import lombok.extern.slf4j.Slf4j;

@Builder
@Slf4j
public record DocumentDTO(
        String id,
        String docId,
        long chunkId,
        String index,
        boolean isActive,
        String content,
        long page,
        double score) {

    public Document toEntity() {
        return Document.builder()
                .id(generateUUID4Doc())
                .docId(this.docId)
                .chunkId(this.chunkId)
                .index(this.index)
                .isActive(this.isActive)
                .content(this.content)
                .page(this.page)
                .build();
    }

    public static DocumentDTO fromEntity(Document document) {
        log.debug("score of document: {}", document.getScore());
        return DocumentDTO.builder()
                .id(document.getId())
                .docId(document.getDocId())
                .chunkId(document.getChunkId())
                .index(document.getIndex())
                .isActive(document.isActive())
                .content(document.getContent())
                .page(document.getPage())
                .score(document.getScore())
                .build();
    }
}
