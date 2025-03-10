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

    @Override
    public Map<String, Object> search(SearchRequestDTO searchRequestDTO) {
        Map<String, Object> result = new HashMap<>();

        float keywordWeight = searchRequestDTO.getKeywordWeightAsFloat();
        float vectorWeight = searchRequestDTO.getVectorWeightAsFloat();
        int k = searchRequestDTO.getKAsInt();
        int page = searchRequestDTO.getPageAsInt();
        int pageSize = searchRequestDTO.getPageSizeAsInt();

        // 1. 검색 서비스 선택
        SearchProcessor searchProcessor = searchFactory.getSearchService(searchRequestDTO.searchType())
                .orElseThrow(() -> new UnsupportedOperationException("지원하지 않는 검색 타입: " + searchRequestDTO.searchType()));

        List<DocumentDTO> documentList;
        if (searchProcessor instanceof HybridSearchProcessor hybridSearchProcessor) {
            printLog(searchRequestDTO, hybridSearchProcessor);

            documentList = hybridSearchProcessor.search(
                    searchRequestDTO.modelName(), searchRequestDTO.knowledgeName(), searchRequestDTO.modelType(), searchRequestDTO.query()
                    , keywordWeight, vectorWeight, k, page, pageSize);
        } else if (searchProcessor instanceof KeywordSearchProcessor keywordSearchProcessor) {
            printLog(searchRequestDTO, keywordSearchProcessor);

            documentList = keywordSearchProcessor.search(
                    searchRequestDTO.modelName(), searchRequestDTO.knowledgeName(), searchRequestDTO.query()
                    , page, pageSize);
        } else if (searchProcessor instanceof VectorSearchProcessor vectorSearchProcessor) {
            printLog(searchRequestDTO, vectorSearchProcessor);

            documentList = vectorSearchProcessor.search(
                    searchRequestDTO.modelName(), searchRequestDTO.knowledgeName(), searchRequestDTO.modelType(), searchRequestDTO.query()
                    , k, page, pageSize);
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
