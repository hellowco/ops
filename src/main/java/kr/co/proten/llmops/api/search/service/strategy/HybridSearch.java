package kr.co.proten.llmops.api.search.service.strategy;

import kr.co.proten.llmops.api.document.dto.DocumentDTO;
import kr.co.proten.llmops.api.search.util.RRFMerger;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class HybridSearch implements HybridSearchProcessor {
    private final Logger log = LoggerFactory.getLogger(this.getClass());
    public static final String SEARCH_TYPE = "hybrid";

    private final KeywordSearchProcessor keywordSearchProcessor;
    private final VectorSearchProcessor vectorSearchProcessor;
    private final RRFMerger rrfMerger;

    public HybridSearch(KeywordSearchProcessor keywordSearchProcessor, VectorSearchProcessor vectorSearchProcessor, RRFMerger rrfMerger) {
        this.keywordSearchProcessor = keywordSearchProcessor;
        this.vectorSearchProcessor = vectorSearchProcessor;
        this.rrfMerger = rrfMerger;
    }

    @Override
    public String getServiceType() { return SEARCH_TYPE; }

    @Override
    public List<DocumentDTO> search(String indexName, String knowledgeName, String modelType, String query, float keywordWeight, float vectorWeight, int k, int page, int pageSize) {
        // 키워드와 벡터 결과 조합
        List<DocumentDTO> keywordResults = keywordSearchProcessor.search(indexName, knowledgeName, query, page, pageSize);
        log.debug("list of keyword document: {}", keywordResults);
        List<DocumentDTO> vectorResults = vectorSearchProcessor.search(indexName, knowledgeName, modelType, query, k, page, pageSize);
        log.debug("list of vector document: {}", vectorResults);

        // 결과 조합 로직
        return rrfMerger.merge(keywordResults,vectorResults, keywordWeight, vectorWeight, pageSize);
    }
}