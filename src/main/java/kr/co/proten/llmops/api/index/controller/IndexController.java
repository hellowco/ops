package kr.co.proten.llmops.api.index.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.opensearch.action.index.IndexRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@RestController
@Tag(name = "Index", description = "index-related operations")
@RequestMapping("/api/index")
public class IndexController {
//    @GetMapping("/")
//    @Operation(summary = "Index List API", description = "인덱스 리스트")
//    public ResponseEntity<?> getIndexList() {
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
//        return ResponseEntity.ok().body(new ArrayList<>());
//    }
//
//    @PostMapping("/")
//    @Operation(summary = "Create Index API", description = "인덱스 생성")
//    public ResponseEntity<?> createIndex(@RequestBody IndexRequest indexRequest) {
//        return ResponseEntity.ok().body(new ArrayList<>());
//    }
//
//    @PutMapping("/")
//    @Operation(summary = "Update Index API", description = "인덱스 수정")
//    public ResponseEntity<?> updateIndex() {
//        return ResponseEntity.ok().body(List.of("file.getName()"));
//    }
}
