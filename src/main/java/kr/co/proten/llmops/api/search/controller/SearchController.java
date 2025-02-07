package kr.co.proten.llmops.api.search.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
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

import java.util.HashMap;
import java.util.Map;

@Tag(name = "Search", description = "검색(키워드, 벡터, 하이브리드)하는 API")
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/search")
public class SearchController {

    private final Logger log = LoggerFactory.getLogger(this.getClass());
    private static final String SUCCESS = "success";
    private final SearchService searchService;

    @PostMapping
    @Operation(
            summary = "검색",
            description = "지정한 지식명에 검색 실행",
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "검색 요청 DTO",
                    required = true,
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = SearchRequestDTO.class)
                    )
            )
    )
    public ResponseEntity<Map<String,Object>> search(@Valid @RequestBody SearchRequestDTO searchRequestDTO) {
        Map<String, Object> resultMap = new HashMap<>();

        log.info("searchrequest: {}", searchRequestDTO);
        searchRequestDTO.validate();
        resultMap.put("status", SUCCESS);
        resultMap.put("msg", String.format("%s 검색 성공!", searchRequestDTO.searchType()));
        resultMap.put("response", searchService.search(searchRequestDTO));

        return ResponseEntity.ok().body(resultMap);
    }
}
