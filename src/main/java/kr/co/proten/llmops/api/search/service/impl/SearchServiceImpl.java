package kr.co.proten.llmops.api.search.service.impl;

import kr.co.proten.llmops.api.document.dto.DocumentDTO;
import kr.co.proten.llmops.api.search.dto.SearchRequestDTO;
import kr.co.proten.llmops.api.search.service.SearchService;
import kr.co.proten.llmops.api.search.service.factory.SearchFactory;
import kr.co.proten.llmops.api.search.service.strategy.HybridSearchProcessor;
import kr.co.proten.llmops.api.search.service.strategy.KeywordSearchProcessor;
import kr.co.proten.llmops.api.search.service.strategy.SearchProcessor;
import kr.co.proten.llmops.api.search.service.strategy.VectorSearchProcessor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class SearchServiceImpl implements SearchService {
    private final Logger log = LoggerFactory.getLogger(this.getClass());

    private final SearchFactory searchFactory;

    public SearchServiceImpl(SearchFactory searchFactory) {
        this.searchFactory = searchFactory;
    }

    @Value("${search.keyword.weight}")
    private float DefaultKeywordWeight;

    @Value("${search.vector.weight}")
    private float DefaultVectorWeight;

    @Value("${search.knn.k}")
    private int DefaultKnnK;

    @Override
    public Map<String, Object> search(SearchRequestDTO searchRequestDTO) {
        Map<String, Object> result = new HashMap<>();

        // 1. 검색 서비스 선택
        SearchProcessor searchProcessor = searchFactory.getSearchService(searchRequestDTO.searchType())
                .orElseThrow(() -> new UnsupportedOperationException("지원하지 않는 검색 타입: " + searchRequestDTO.searchType()));

        List<DocumentDTO> documentList;
        if (searchProcessor instanceof HybridSearchProcessor) {
            printLog(searchRequestDTO, searchProcessor);
            float keywordWeight = searchRequestDTO.keywordWeight().orElse(DefaultKeywordWeight);
            float vectorWeight = searchRequestDTO.vectorWeight().orElse(DefaultVectorWeight);
            int k = searchRequestDTO.k().orElse(DefaultKnnK);
            documentList = ((HybridSearchProcessor) searchProcessor).search(searchRequestDTO.indexName(), searchRequestDTO.knowledgeName(), searchRequestDTO.modelType(), searchRequestDTO.query(), keywordWeight, vectorWeight, k);
        } else if (searchProcessor instanceof KeywordSearchProcessor) {
            printLog(searchRequestDTO, searchProcessor);
            documentList = ((KeywordSearchProcessor) searchProcessor).search(searchRequestDTO.indexName(), searchRequestDTO.knowledgeName(), searchRequestDTO.query());
        } else if (searchProcessor instanceof VectorSearchProcessor) {
            printLog(searchRequestDTO, searchProcessor);
            int k = searchRequestDTO.k().orElse(DefaultKnnK);
            documentList = ((VectorSearchProcessor) searchProcessor).search(searchRequestDTO.indexName(), searchRequestDTO.knowledgeName(), searchRequestDTO.modelType(), searchRequestDTO.query(), k);
        } else {
            throw new UnsupportedOperationException("지원하지 않는 검색 프로세서 타입: " + searchProcessor.getServiceType());
        }

        // 결과 반환
        result.put("status", "success");
        result.put("message", "검색 성공!");
        result.put("response", documentList);

        return result;
    }

    private void printLog(SearchRequestDTO searchRequestDTO, SearchProcessor searchProcessor) {
        log.info("{}으로 검색 {} 사용", searchRequestDTO.searchType(), searchProcessor.getClass().getSimpleName());
    }
}
