package kr.co.proten.llmops.api.document.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import kr.co.proten.llmops.api.document.dto.DocumentDTO;
import kr.co.proten.llmops.api.document.service.ChunkService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@Tag(name = "Index", description = "index-related operations")
@RequestMapping("/api")
public class ChunkController {
    private final Logger log = LoggerFactory.getLogger(this.getClass());

    private final ChunkService chunkService;

    public ChunkController(ChunkService chunkService) {
        this.chunkService = chunkService;
    }

    @PostMapping("/")
    @Operation(summary = "문서 내 청크 추가", description = "Create Chunk API")
    public ResponseEntity<?> createChunk(
            @RequestParam(value = "indexName") String indexName,
            @RequestParam(value = "knowledgeName") String knowledgeName,
            @RequestParam(value = "docId") String docId,
            @RequestParam(value = "content") String content,
            @RequestParam(value = "modelType", defaultValue = "ProsLLM") String modelType
            ) throws Exception {
        Map<String, Object> resultMap;
        resultMap = chunkService.createChunk(indexName, knowledgeName, docId, content, modelType);
        return ResponseEntity.ok().body(resultMap);
    }

    @GetMapping("/{chunkId}")
    @Operation(summary = "하나의 청크 내용 보기", description = "View Chunk API")
    public ResponseEntity<?> getChunk(
            @PathVariable(value="chunkId") long chunkId,
            @RequestParam(value = "indexName") String indexName,
            @RequestParam(value = "knowledgeName") String knowledgeName,
            @RequestParam(value = "docId") String docId
    ) throws Exception {
        Map<String, Object> resultMap;
        resultMap = chunkService.readChunk(indexName, knowledgeName, docId, chunkId);
        return ResponseEntity.ok().body(resultMap);
    }

    @PutMapping("/")
    @Operation(summary = "문서 내 청크 수정", description = "Update Chunk API")
    public ResponseEntity<?> updateChunk(
            @RequestParam(value = "indexName") String indexName,
            @RequestParam(value = "knowledgeName") String knowledgeName,
            @RequestParam(value = "docId") String docId,
            @RequestParam(value = "chunkId") long chunkId,
            @RequestParam(value = "content") String content,
            @RequestParam(value = "modelType", defaultValue = "ProsLLM") String modelType
    ) throws Exception {
        Map<String, Object> resultMap;
        resultMap = chunkService.updateChunk(indexName, knowledgeName, docId, chunkId, content, modelType);
        return ResponseEntity.ok().body(resultMap);
    }

    @DeleteMapping("/")
    @Operation(summary = "문서 내 청크 삭제", description = "Delete Chunk API")
    public ResponseEntity<?> deleteChunk(
            @RequestParam(value = "modelName", defaultValue = "llmops") String targetIndex,
            @RequestParam(value = "indexName", defaultValue = "test") String knowledgeName,
            @RequestParam(value = "docId") String docId,
            @RequestParam(value = "chunkId") long chunkId
    ) throws Exception {
        Map<String, Object> resultMap;
        resultMap = chunkService.deleteChunk(targetIndex, knowledgeName, docId, chunkId);
        return ResponseEntity.ok().body(resultMap);
    }
}
