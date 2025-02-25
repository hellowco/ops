package kr.co.proten.llmops.api.knowledge.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import kr.co.proten.llmops.api.app.dto.request.AppStateDTO;
import kr.co.proten.llmops.api.knowledge.service.KnowledgeService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@RestController
@Tag(name = "Knowledge", description = "지식 리스트/생성/수정/삭제 API")
@RequestMapping("/api/knowledge")
public class KnowledgeController {

    private final KnowledgeService knowledgeService;

    public KnowledgeController(KnowledgeService knowledgeService) {
        this.knowledgeService = knowledgeService;
    }

/*    @PostMapping("/index")
    @Operation(summary = "Create Index API", description = "인덱스 생성 API")
    public ResponseEntity<?> createIndex(@RequestParam(value="indexName") String indexName) throws IOException {
        return ResponseEntity.ok().body(knowledgeService.createIndexWithMapping(indexName));
    }

    @DeleteMapping("/index")
    @Operation(summary = "Delete Index API", description = "인덱스 삭제 API")
    public ResponseEntity<?> deleteIndex(@RequestParam(value="indexName") String indexName) throws IOException {
        return ResponseEntity.ok().body(knowledgeService.deleteIndex(indexName));
    }*/

    @PostMapping("/")
    @Operation(summary = "지식 추가", description = "Create Knowledge API")
    public ResponseEntity<Map<String,Object>> createKnowledge(
            @RequestParam(value = "modelName") String indexName,
            @RequestParam(value = "knowledgeName") String knowledgeName,
            @RequestParam(value = "description") String description
    ) throws Exception {
        Map<String, Object> resultMap;

        resultMap = knowledgeService.createKnowledge(indexName, knowledgeName, description);

        return ResponseEntity.ok().body(resultMap);
    }

    @GetMapping("/")
    @Operation(summary = "지식 리스트", description = "Knowledge List API")
    public ResponseEntity<Map<String,Object>> getKnowledgeList() {
        Map<String, Object> resultMap;

        resultMap = knowledgeService.getKnowledgeList();

        return ResponseEntity.ok().body(resultMap);
    }

    @PutMapping("/")
    @Operation(summary = "지식 수정", description = "Update Knowledge API")
    public ResponseEntity<Map<String,Object>> updateKnowledge(
            @RequestParam(value = "knowledgeId") String knowledgeId,
            @RequestParam(value = "description") String description
    ) {
        Map<String, Object> resultMap;

        resultMap = knowledgeService.updateKnowledge(knowledgeId, description);

        return ResponseEntity.ok().body(resultMap);
    }

    @DeleteMapping("/")
    @Operation(summary = "지식 삭제", description = "Delete Knowledge API")
    public ResponseEntity<Map<String,Object>> deleteKnowledge(
            @RequestParam(value = "knowledgeId") String knowledgeId
    ) throws Exception {
        Map<String, Object> resultMap;

        resultMap = knowledgeService.deleteKnowledge(knowledgeId);

        return ResponseEntity.ok().body(resultMap);
    }
}
