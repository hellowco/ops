package kr.co.proten.llmops.api.document.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import kr.co.proten.llmops.api.document.dto.MetadataDTO;
import kr.co.proten.llmops.api.document.service.DocumentService;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@Tag(name = "Index", description = "index-related operations")
@RequestMapping("/api")
public class DocumentController {

    private final DocumentService documentService;

    public DocumentController(DocumentService documentService) {
        this.documentService = documentService;
    }

//    @GetMapping("/{indexId}/{docId}/search")
//    @Operation(summary = "Test Document Search API", description = "해당 문서 테스트 검색")
//    public ResponseEntity<?> getDocumentSearch(@PathVariable("indexId") String indexId) {
//        return ResponseEntity.ok().body(Arrays.asList("search", "document: " + indexId));
//    }

    @GetMapping("/doc/{docId}")
    @Operation(summary = "Document Detail API", description = "문서 상세 보기 (메타데이터)")
    public ResponseEntity<?> getDocumentDetail(
            @PathVariable("docId") String docId,
            @RequestParam(value = "indexName", defaultValue = "llmops") String targetIndex
    ) throws Exception {
        Map<String, Object> resultMap = new HashMap<>();
        resultMap = documentService.getDocument(targetIndex,docId);

        return ResponseEntity.ok().body(Arrays.asList("detail", "document: " + docId));
    }

//    @GetMapping("/{indexId}")
//    @Operation(summary = "Document List API", description = "인덱스 내 문서 리스트")
//    public ResponseEntity<?> getDocumentList(@PathVariable("indexId") String indexId) {
//         /*
//        인덱스명으로 서치해서 유니크한 리스트만 가져오기
//        GET summary_file/_search
//        {
//          "size": 0,
//          "aggs": {
//            "unique_indices": {
//              "terms": {
//                "field": "_index",
//                "size": 1000
//              }
//            }
//          }
//        }
//        * */
//
//        return ResponseEntity.ok().body(Arrays.asList("list", "list of " + indexId));
//    }

    @PostMapping(value = "/doc", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @Operation(summary = "Upload Document API", description = "문서 업로드")
    public ResponseEntity<?> uploadDocument(@RequestPart(value="file") MultipartFile file) throws Exception {
        Map<String, Object> resultMap;
        resultMap = documentService.uploadFile(file);

        return ResponseEntity.ok().body(resultMap);
    }

    @PostMapping(value = "/doc/index")
    @Operation(summary = "Index Document API", description = "문서 인덱싱")
    public ResponseEntity<?> indexDocument(
            @RequestParam(value = "fileName", defaultValue = "test.txt") String fileName,
            @RequestParam(value = "targetIndex", defaultValue = "llmops") String targetIndex,
            @RequestParam(value = "chunkSize", defaultValue = "200") int chunkSize,
            @RequestParam(value = "overlapSize", defaultValue = "50") int overlapSize,
            @RequestParam(value = "modelType", defaultValue = "ProsLLM") String modelType,
            @RequestParam(value = "pluginKeys", defaultValue = "removeWhitespace") List<String> processingKeys) throws Exception {
        Map<String, Object> resultMap;

        System.out.printf("%s, %s, %d, %d, %s, %s%n", targetIndex, fileName, chunkSize, overlapSize, modelType, processingKeys);

        if (fileName == null || fileName.isEmpty()) {
            fileName = "test.txt";
        }

        resultMap = documentService.uploadDocument(targetIndex, fileName, chunkSize, overlapSize, modelType, processingKeys);

        return ResponseEntity.ok().body(resultMap);
    }

    @PutMapping("/doc")
    @Operation(summary = "Update Document API", description = "해당 문서 수정")
    public ResponseEntity<?> updateDocument(
            @RequestParam(value = "indexName", defaultValue = "llmops") String targetIndex,
            @RequestBody MetadataDTO metadataDTO
            ) {
        Map<String, Object> resultMap;
        resultMap = documentService.updateDocument(targetIndex,metadataDTO);
        return ResponseEntity.ok().body(resultMap);
    }

    @DeleteMapping("/doc")
    @Operation(summary = "Delete Document API", description = "해당 문서 삭제")
    public ResponseEntity<?> deleteDocument(
            @RequestParam(value = "indexName", defaultValue = "llmops") String targetIndex,
            @RequestParam(value = "docId") String docId
    ) {
        Map<String, Object> resultMap;
        resultMap = documentService.deleteDocument(targetIndex,docId);
        return ResponseEntity.ok().body(resultMap);
    }

    /*
    POST /documents: 문서 업로드
    요청: 파일 업로드 및 메타데이터 정보
    호출 서비스:
    ChunkService: 문서를 청크로 나누기
    EmbeddingService: 문서의 벡터 임베딩 생성
    DocumentService: 문서를 저장(DB와 OpenSearch)

    PUT /documents/{documentId}: 문서 수정
    요청: 문서의 내용 및 메타데이터 수정
    호출 서비스:
    DocumentService: 문서 수정 로직 처리

    DELETE /documents/{documentId}: 문서 삭제
    요청: 삭제할 문서 ID
    호출 서비스:
    DocumentService: 문서를 DB와 OpenSearch에서 삭제

    GET /documents: 문서 리스트 조회
    요청: 필터 및 페이지네이션 옵션
    호출 서비스:
    DocumentService: 문서 목록 조회(DB 기반)

    GET /documents/{documentId}: 특정 문서 상세 조회
    요청: 문서 ID
    호출 서비스:
    DocumentService: 특정 문서 정보 조회
     */

}
