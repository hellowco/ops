package kr.co.proten.llmops.api.search.controller;

import kr.co.proten.llmops.api.search.dto.SearchRequestDTO;
import kr.co.proten.llmops.api.search.service.SearchService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/api/search")
public class SearchController {
    private final Logger log = LoggerFactory.getLogger(this.getClass());

    private final SearchService searchService;

    public SearchController(SearchService searchService) {
        this.searchService = searchService;
    }

    @PostMapping
    public ResponseEntity<?> search(@RequestBody(required = false) SearchRequestDTO searchRequestDTO) {
        Map<String, Object> resultMap;

        log.info("searchrequest: {}", searchRequestDTO);
        resultMap = searchService.search(searchRequestDTO);
        return ResponseEntity.ok().body(resultMap);
    }
}
