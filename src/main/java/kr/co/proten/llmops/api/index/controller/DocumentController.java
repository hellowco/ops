package kr.co.proten.llmops.api.index.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import kr.co.proten.llmops.api.index.service.IndexService;
import org.opensearch.action.index.IndexRequest;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

@RestController
@Tag(name = "Index", description = "index-related operations")
@RequestMapping("/api")
public class DocumentController {

    private final IndexService indexService;

    public DocumentController(IndexService indexService) {
        this.indexService = indexService;
    }

    @GetMapping("/{indexId}/{docId}/search")
    @Operation(summary = "Test Document Search API", description = "해당 문서 테스트 검색")
    public ResponseEntity<?> getDocumentSearch(@PathVariable("indexId") String indexId) {
        return ResponseEntity.ok().body(Arrays.asList("search", "document: " + indexId));
    }

    @GetMapping("/{indexId}/{docId}")
    @Operation(summary = "Document Detail API", description = "문서 상세 보기 (메타데이터)")
    public ResponseEntity<?> getDocumentDetail(@PathVariable("docId") String docId) {
        return ResponseEntity.ok().body(Arrays.asList("detail", "document: " + docId));
    }

    @GetMapping("/{indexId}")
    @Operation(summary = "Document List API", description = "인덱스 내 문서 리스트")
    public ResponseEntity<?> getDocumentList(@PathVariable("indexId") String indexId) {
         /*
        인덱스명으로 서치해서 유니크한 리스트만 가져오기
        GET summary_file/_search
        {
          "size": 0,
          "aggs": {
            "unique_indices": {
              "terms": {
                "field": "_index",
                "size": 1000
              }
            }
          }
        }
        * */

        return ResponseEntity.ok().body(Arrays.asList("list", "list of " + indexId));
    }

    @PostMapping(value = "/doc", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @Operation(summary = "Upload Document API", description = "문서 업로드")
    public ResponseEntity<?> uploadDocument(@RequestPart(value="file") MultipartFile file) throws Exception {
        Map<String, Object> resultMap = new HashMap<>();
        resultMap = indexService.uploadFile(file);

        return ResponseEntity.ok().body(resultMap);
    }

    @PutMapping("/doc")
    @Operation(summary = "Update Document API", description = "해당 문서 수정")
    public ResponseEntity<?> updateDocument() {
        return ResponseEntity.ok().body(Arrays.asList("modify", "document"));
    }

    @DeleteMapping("/doc")
    @Operation(summary = "Delete Document API", description = "해당 문서 삭제")
    public ResponseEntity<?> deleteDocument() {
        return ResponseEntity.ok().body(Arrays.asList("delete", "document"));
    }

}
