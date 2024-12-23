package kr.co.proten.llmops.api.search.service.factory;

import kr.co.proten.llmops.api.search.service.strategy.SearchProcessor;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.stream.Collectors;

@Component
public class SearchFactory {
    private final Map<String, SearchProcessor> searchServiceMap;

    public SearchFactory(List<SearchProcessor> searchProcessors) {
        this.searchServiceMap = searchProcessors.stream()
                .collect(Collectors.toMap(SearchProcessor::getServiceType, s -> s));
    }

    public Optional<SearchProcessor> getSearchService(String searchType) {
        return Optional.ofNullable(searchServiceMap.get(searchType));
    }
}