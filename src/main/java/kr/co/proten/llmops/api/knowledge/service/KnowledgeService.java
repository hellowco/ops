package kr.co.proten.llmops.api.knowledge.service;

import java.util.Map;

public interface KnowledgeService {

    String createIndex(String indexName, int dimension);

    boolean deleteIndex(String indexName);

    Map<String, Object> getKnowledgeList();

    Map<String, Object> createKnowledge(String indexName, String knowledgeName, String description);

    Map<String, Object> updateKnowledge(String id, String description);

    Map<String, Object> deleteKnowledge(String knowledgeId);
}
