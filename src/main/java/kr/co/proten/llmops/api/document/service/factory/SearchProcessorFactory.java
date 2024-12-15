package kr.co.proten.llmops.api.document.service.factory;

import kr.co.proten.llmops.api.document.service.strategy.search.SearchProcessor;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Component
public class SearchProcessorFactory {
    private final Map<String, SearchProcessor> chunkServiceMap = new HashMap<>();

    public SearchProcessorFactory(List<SearchProcessor> EmbeddingProcessor) {
        EmbeddingProcessor.forEach(s -> chunkServiceMap.put(s.getServiceType(), s));
    }

    public Optional<SearchProcessor> getSearchService(String searchType) {
        return Optional.ofNullable(chunkServiceMap.get(searchType));
    }
}