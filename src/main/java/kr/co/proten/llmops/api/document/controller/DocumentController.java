package kr.co.proten.llmops.api.document.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import kr.co.proten.llmops.api.document.service.DocumentService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Map;

@RestController
@Tag(name = "Document", description = "문서 관리 API")
@RequestMapping("/api")
public class DocumentController {
    private final Logger log = LoggerFactory.getLogger(this.getClass());

    private final DocumentService documentService;

    public DocumentController(DocumentService documentService) {
        this.documentService = documentService;
    }

    @GetMapping("/doc/list")
    @Operation(summary = "문서 리스트 보기 (해당 인덱스에 있는 모든 문서)", description = "Document List API")
    public ResponseEntity<Map<String, Object>> getDocumentList(
            @RequestParam(value = "modelName", defaultValue = "llmops") String targetIndex,
            @RequestParam(value = "knowledgeName", defaultValue = "test") String knowledgeName,
            @RequestParam(value = "pageNo", defaultValue = "1") int pageNo,
            @RequestParam(value = "pageSize", defaultValue = "10") int pageSize) throws Exception {
        Map<String, Object> resultMap;
        resultMap = documentService.getDocumentList(targetIndex, knowledgeName, pageNo, pageSize);

        return ResponseEntity.ok().body(resultMap);
    }

    @GetMapping("/doc/{docId}")
    @Operation(summary = "문서 보기 (문서의 모든 청크 리스트)", description = "Document Chunk List API")
    public ResponseEntity<Map<String, Object>> getDocumentDetail(
            @PathVariable("docId") String docId,
            @RequestParam(value = "modelName", defaultValue = "llmops") String targetIndex,
            @RequestParam(value = "knowledgeName", defaultValue = "test") String knowledgeName,
            @RequestParam(value = "pageNo", defaultValue = "1") int pageNo,
            @RequestParam(value = "pageSize", defaultValue = "10") int pageSize) throws Exception {
        Map<String, Object> resultMap;
        resultMap = documentService.getDocument(targetIndex, knowledgeName, docId, pageNo, pageSize);

        return ResponseEntity.ok().body(resultMap);
    }

    @GetMapping("/metadata/{docId}")
    @Operation(summary = "문서 상세 보기 (메타데이터)", description = "Document Detail(Metadata) API")
    public ResponseEntity<Map<String, Object>> getDocumentMetadata(
            @PathVariable("docId") String docId,
            @RequestParam(value = "modelName", defaultValue = "llmops") String targetIndex,
            @RequestParam(value = "knowledgeName", defaultValue = "test") String knowledgeName) throws Exception {
        Map<String, Object> resultMap;
        resultMap = documentService.getDocumentMetadata(targetIndex, knowledgeName, docId);

        return ResponseEntity.ok().body(resultMap);
    }


    @PostMapping(value = "/doc", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @Operation(summary = "문서 업로드", description = "Upload Document API")
    public ResponseEntity<Map<String, Object>> uploadDocument(@RequestPart(value = "file") MultipartFile file) throws Exception {
        Map<String, Object> resultMap;
        resultMap = documentService.uploadFile(file);

        return ResponseEntity.ok().body(resultMap);
    }

    @PostMapping(value = "/doc/index")
    @Operation(summary = "문서 인덱싱", description = "Index Document API")
    public ResponseEntity<Map<String, Object>> indexDocument(
            @RequestParam(value = "fileName", defaultValue = "test.txt") String fileName,
            @RequestParam(value = "modelName", defaultValue = "llmops") String targetIndex,
            @RequestParam(value = "knowledgeName", defaultValue = "test") String knowledgeName,
            @RequestParam(value = "chunkSize", defaultValue = "200") int chunkSize,
            @RequestParam(value = "overlapSize", defaultValue = "50") int overlapSize,
            @RequestParam(value = "modelType", defaultValue = "ProsLLM") String modelType,
            @RequestParam(value = "pluginKeys", defaultValue = "removeWhitespace") List<String> processingKeys) throws Exception {
        Map<String, Object> resultMap;

        if (fileName == null || fileName.isEmpty()) {
            fileName = "test.txt";
        }

        resultMap = documentService.uploadDocument(targetIndex, knowledgeName, fileName, chunkSize, overlapSize, modelType, processingKeys);

        return ResponseEntity.ok().body(resultMap);
    }

    @PutMapping("/doc")
    @Operation(summary = "해당 문서 메타데이터 수정", description = "Update Document API")
    public ResponseEntity<Map<String, Object>> updateDocument(
            @RequestParam(value = "modelName") String targetIndex,
            @RequestParam(value = "knowledgeName") String knowledgeName,
            @RequestParam(value = "docId") String docId,
            @RequestParam(value = "description") String description
    ) {
        Map<String, Object> resultMap;
        resultMap = documentService.updateDocument(targetIndex, knowledgeName, docId, description);
        return ResponseEntity.ok().body(resultMap);
    }

    @DeleteMapping("/doc")
    @Operation(summary = "해당 문서 삭제", description = "Delete Document API")
    public ResponseEntity<Map<String, Object>> deleteDocument(
            @RequestParam(value = "modelName", defaultValue = "llmops") String targetIndex,
            @RequestParam(value = "knowledgeName", defaultValue = "test") String knowledgeName,
            @RequestParam(value = "docId") String docId) {
        Map<String, Object> resultMap;
        resultMap = documentService.deleteDocument(targetIndex, knowledgeName, docId);
        return ResponseEntity.ok().body(resultMap);
    }

    @PutMapping("/doc/activity")
    @Operation(summary = "해당 문서 활성여부 변경", description = "Update Document Activeness API")
    public ResponseEntity<Map<String, Object>> updateDocumentActiveness(
            @RequestParam(value = "modelName") String targetIndex,
            @RequestParam(value = "knowledgeName") String knowledgeName,
            @RequestParam(value = "docId") String docId,
            @RequestParam(value = "active") boolean isActive
    ) {
        Map<String, Object> resultMap;
        resultMap = documentService.updateDocument(targetIndex, knowledgeName, docId, isActive);
        return ResponseEntity.ok().body(resultMap);
    }
}
