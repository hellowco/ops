package kr.co.proten.llmops.api.search.service.strategy;

import kr.co.proten.llmops.api.document.dto.DocumentDTO;
import kr.co.proten.llmops.api.document.entity.Document;
import kr.co.proten.llmops.api.search.repository.SearchRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class KeywordSearch implements KeywordSearchProcessor {
    private final Logger log = LoggerFactory.getLogger(this.getClass());
    public static final String SEARCH_TYPE = "keyword";

    private final SearchRepository searchRepository;

    public KeywordSearch(SearchRepository searchRepository) {
        this.searchRepository = searchRepository;
    }

    @Override
    public String getServiceType() { return SEARCH_TYPE; }

    @Override
    public List<DocumentDTO> search(String indexName, String knowledgeName,String query, int page, int pageSize) {
        List<Document>  documentList = searchRepository.keywordSearch(indexName, knowledgeName, query, page, pageSize);
        return documentList.stream()
                .map(DocumentDTO::fromEntity)
                .toList();
    }
}