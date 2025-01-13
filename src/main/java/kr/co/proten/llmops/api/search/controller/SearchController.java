package kr.co.proten.llmops.api.search.controller;

import io.swagger.v3.oas.annotations.tags.Tag;
import kr.co.proten.llmops.api.search.dto.SearchRequestDTO;
import kr.co.proten.llmops.api.search.service.SearchService;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@Tag(name = "Search", description = "검색(키워드, 벡터, 하이브리드)하는 API")
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/search")
public class SearchController {

    private final Logger log = LoggerFactory.getLogger(this.getClass());
    private final SearchService searchService;

    @PostMapping
    public ResponseEntity<Map<String,Object>> search(@RequestBody(required = false) SearchRequestDTO searchRequestDTO) {
        Map<String, Object> resultMap;

        log.info("searchrequest: {}", searchRequestDTO);
        resultMap = searchService.search(searchRequestDTO);

        return ResponseEntity.ok().body(resultMap);
    }
}
