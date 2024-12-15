package kr.co.proten.llmops.api.document.util;

import kr.co.proten.llmops.api.document.entity.Document;
import kr.co.proten.llmops.api.document.entity.Metadata;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static kr.co.proten.llmops.core.helpers.UUIDGenerator.generateUUID;

public class DocumentBuilder {
    public List<Document> createDocuments(
        String indexName, 
        String docId, 
        List<String> chunks, 
        List<List<Double>> embeddings
    ) {
        validateInput(chunks, embeddings);
        
        return IntStream.range(0, chunks.size())
            .mapToObj(i -> buildDocument(
                indexName, 
                docId, 
                chunks.get(i), 
                embeddings != null ? embeddings.get(i) : null
            ))
            .collect(Collectors.toList());
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
        String indexName, 
        String docId, 
        String content, 
        List<Double> contentVec
    ) {
        return Document.builder()
            .index(indexName)
            .id(generateUUID())
            .docId(docId)
            .content(content)
            .contentVec(contentVec)
            .isActive(true)
            .page(0L)
            .build();
    }

    public Metadata buildMetadata(
            String indexName,
            String docId
    ) {
        return Metadata.builder()
                .id(generateUUID())
                .docId(docId)
                .index(indexName)
                .isActive(true)
                .lastUpdatedDate("2024-12-12")
                .convertDate("2024-12-11")
                .orgFileName("original_file.txt")
                .orgFilePath("/path/to/original/file")
                .totalPage(100)
                .chunkSize(1024)
                .chunkNum(10)
                .pdfFileName("document.pdf")
                .pdfFilePath("/path/to/pdf/file")
                .userId("user_001")
                .version("1.0")
                .build();
    }
}