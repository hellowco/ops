package kr.co.proten.llmops.api.document.util;

import kr.co.proten.llmops.api.document.entity.Document;
import kr.co.proten.llmops.api.document.entity.Metadata;
import kr.co.proten.llmops.core.helpers.DateUtil;

import java.util.List;
import java.util.stream.IntStream;

import static kr.co.proten.llmops.core.helpers.UUIDGenerator.generateUUID;
import static kr.co.proten.llmops.core.helpers.UUIDGenerator.generateUUID4Doc;

public class DocumentBuilder {
    public Document createDocument(
            String knowledgeName,
            String docId,
            long chunkId,
            String chunk,
            List<Double> embeddings
    ) {
        return buildDocument(
                knowledgeName,
                docId,
                chunkId, // chunkId는 문서에서 청크된 순서
                chunk,
                embeddings
                );
    }

    public List<Document> createDocuments(
        String knowledgeName,
        String docId,
        List<String> chunks,
        List<List<Double>> embeddings
    ) {
        validateInput(chunks, embeddings);
        
        return IntStream.range(0, chunks.size())
            .mapToObj(i -> buildDocument(
                knowledgeName,
                docId,
                i + 1L, // chunkId는 문서에서 청크된 순서
                chunks.get(i), 
                embeddings != null ? embeddings.get(i) : null)
            ).toList();
    }
    
    private void validateInput(List<String> chunks, List<List<Double>> embeddings) {
        if (chunks == null) {
            throw new IllegalArgumentException("Chunks cannot be null");
        }
        
        // Optional check to ensure embeddings size matches chunks if embeddings is not null
        if (embeddings != null && chunks.size() != embeddings.size()) {
            throw new IllegalArgumentException("Number of chunks must match number of embeddings");
        }
    }

    private Document buildDocument(
        String knowledgeName,
        String docId,
        long chunkId,
        String content,
        List<Double> contentVec
    ) {
        return Document.builder()
            .index(knowledgeName)
            .id(generateUUID4Doc())
            .docId(docId)
            .chunkId(chunkId)
            .content(content)
            .contentVec(contentVec)
            .isActive(true)
            .page(0L)
            .build();
    }

    public Metadata buildMetadata(
            String knowledgeName,
            String docId,
            String filename,
            String filepath,
            int chunkSize,
            int chunkNum
    ) {
        return Metadata.builder()
                .id(generateUUID())
                .docId(docId)
                .index(knowledgeName)
                .isActive(true)
                .lastUpdatedDate(DateUtil.generateCurrentTimestamp4OpenSearch())
                .convertDate(DateUtil.generateCurrentTimestamp4OpenSearch())
                .orgFileName(filename)
                .orgFilePath(filepath)
                .totalPage(0)
                .chunkSize(chunkSize)
                .chunkNum(chunkNum)
                .pdfFileName("N/A")
                .pdfFilePath("N/A")
                .userId("user_001")
                .version("1.0")
                .build();
    }
}