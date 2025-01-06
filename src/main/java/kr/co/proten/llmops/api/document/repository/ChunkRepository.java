package kr.co.proten.llmops.api.document.repository;

import kr.co.proten.llmops.api.document.entity.Document;

import java.util.Map;

public interface ChunkRepository {
    Document saveChunk(String indexName, Document document) throws Exception;

    Document getChunkByChunkId(String indexName, String knowledgeName, String docId, long chunkId) throws Exception;

    String updateChunkByChunkId(String indexName, String docId, Map<String, Object> updatedFields) throws Exception;

    void deleteChunkByChunkId(String indexName, String knowledgeName, String docId, long chunkId) throws Exception;
}