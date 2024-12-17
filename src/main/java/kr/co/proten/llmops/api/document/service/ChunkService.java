package kr.co.proten.llmops.api.document.service;

import kr.co.proten.llmops.api.document.dto.DocumentDTO;

import java.util.Map;

public interface ChunkService {

    Map<String, Object> createChunk(String indexName, String knowledgeName, String docId, String content, String modelType) throws Exception;

    Map<String, Object> readChunk(String indexName, String knowledgeName, String docId, long chunkId) throws Exception;

    Map<String, Object> updateChunk(String indexName, String knowledgeName, String docId, long chunkId, String content, String modelType) throws Exception;

    Map<String, Object> deleteChunk(String indexName, String knowledgeName, String docId, long chunkId) throws Exception;
}
