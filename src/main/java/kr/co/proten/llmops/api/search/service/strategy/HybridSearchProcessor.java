package kr.co.proten.llmops.api.search.service.strategy;

import kr.co.proten.llmops.api.document.dto.DocumentDTO;

import java.util.List;

public interface HybridSearchProcessor extends SearchProcessor {
    List<DocumentDTO> search(String indexName, String knowledgeName, String modelType, String query, float keywordWeight, float vectorWeight, int k, int page, int pageSize);
}
