package kr.co.proten.llmops.api.search.service;

import kr.co.proten.llmops.api.search.dto.SearchRequestDTO;

import java.util.Map;

public interface SearchService {
    Map<String, Object> search(SearchRequestDTO searchRequestDTO);
}
