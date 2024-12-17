package kr.co.proten.llmops.api.document.dto;

import static kr.co.proten.llmops.core.helpers.UUIDGenerator.generateUUID;

import kr.co.proten.llmops.api.document.entity.Document;

import lombok.Builder;
@Builder
public record DocumentDTO(
        String id,
        String docId,
        long chunkId,
        String index,
        boolean isActive,
        String content,
        long page) {

    public Document toEntity() {
        return Document.builder()
                .id(generateUUID())
                .docId(this.docId)
                .chunkId(this.chunkId)
                .index(this.index)
                .isActive(this.isActive)
                .content(this.content)
                .page(this.page)
                .build();
    }

    public DocumentDTO fromEntity(Document document) {
        return DocumentDTO.builder()
                .id(document.getId())
                .docId(document.getDocId())
                .chunkId(document.getChunkId())
                .index(document.getIndex())
                .isActive(document.isActive())
                .content(document.getContent())
                .page(document.getPage())
                .build();
    }
}
