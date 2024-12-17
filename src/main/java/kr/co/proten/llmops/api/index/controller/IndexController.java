package kr.co.proten.llmops.api.index.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import kr.co.proten.llmops.api.document.service.DocumentService;
import kr.co.proten.llmops.api.index.service.IndexService;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@Tag(name = "Index", description = "인덱스 생성/삭제")
@RequestMapping("/api")
public class IndexController {

    private final IndexService indexService;

    public IndexController(IndexService indexService) {
        this.indexService = indexService;
    }

//    @PostMapping("/index")
//    @Operation(summary = "Create Index API", description = "인덱스 생성 API")
//    public ResponseEntity<?> createIndex(@RequestParam(value="indexName") String indexName) throws IOException {
//        return ResponseEntity.ok().body(indexService.createIndexWithMapping(indexName));
//    }
//
//    @DeleteMapping("/index")
//    @Operation(summary = "Delete Index API", description = "인덱스 삭제 API")
//    public ResponseEntity<?> deleteIndex(@RequestParam(value="indexName") String indexName) throws IOException {
//        return ResponseEntity.ok().body(indexService.deleteIndex(indexName));
//    }

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
}
