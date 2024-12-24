package kr.co.proten.llmops.api.knowledge.service;

import kr.co.proten.llmops.api.knowledge.dto.KnowledgeDTO;

import java.io.IOException;
import java.util.Map;

public interface KnowledgeService {
    boolean createIndexWithMapping(String indexName) throws IOException;
    boolean deleteIndex(String indexName) throws IOException;

    Map<String, Object> getKnowledgeList();

    Map<String, Object> createKnowledge(String indexName, String knowledgeName, String description);

    Map<String, Object> updateKnowledge(String id, String description);

    Map<String, Object> deleteKnowledge(String knowledgeId);
}
