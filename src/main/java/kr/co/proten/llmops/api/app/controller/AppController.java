package kr.co.proten.llmops.api.app.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import kr.co.proten.llmops.api.app.dto.request.AppCreateDTO;
import kr.co.proten.llmops.api.app.dto.request.AppUpdateDTO;
import kr.co.proten.llmops.api.app.dto.request.AppSearchDTO;
import kr.co.proten.llmops.api.app.service.AppService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@Tag(name = "App", description = "앱 생성, 검색, 수정, 삭제하는 API")
@RestController
@RequestMapping("/api/app")
public class AppController {

    private final AppService appService;

    public AppController(AppService appService) {
        this.appService = appService;
    }

    private static final String SUCCESS = "success";

    @PostMapping
    @Operation(summary = "앱 생성", description = "워크스페이스 ID, 앱 이름, 앱 설명 기반으로 앱 생성하는 API")
    public ResponseEntity<Map<String, Object>> createApp(
            @RequestBody AppCreateDTO appCreateDTO
    ) {
        Map<String, Object> resultMap = new HashMap<>();

        resultMap.put("status", SUCCESS);
        resultMap.put("msg", "앱 생성 성공!");
        resultMap.put("response", appService.createApp(appCreateDTO));

        return ResponseEntity.ok(resultMap);
    }

    @GetMapping("/{app_id}")
    @Operation(summary = "하나의 앱 반환", description = "앱 ID로 앱 객체 반환하는 API")
    public ResponseEntity<Map<String, Object>> getAppById(
            @PathVariable String app_id
    ) {
        Map<String, Object> resultMap = new HashMap<>();

        resultMap.put("status", SUCCESS);
        resultMap.put("msg", String.format("앱 %s 반환 성공!", app_id));
        resultMap.put("response", appService.getAppById(app_id));

        return ResponseEntity.ok(resultMap);
    }

    @GetMapping
    @Operation(summary = "워크스페이스 내의 모든 앱 리스트", description = "워크스페이스 ID로 앱 객체 리스트 반환하는 API")
    public ResponseEntity<Map<String, Object>> getAllApps(
            @RequestParam String workspaceId
    ) {
        Map<String, Object> resultMap = new HashMap<>();

        resultMap.put("status", SUCCESS);
        resultMap.put("msg", String.format("워크스페이스: %s의 앱리스트 반환 성공!", workspaceId));
        resultMap.put("response", appService.getAllApps(workspaceId));

        return ResponseEntity.ok(resultMap);
    }

//    @PostMapping("/search")
//    public ResponseEntity<Map<String, Object>> getAppByName(
//            @RequestBody AppSearchDTO appSearchDTO
//    ) {
//        Map<String, Object> resultMap = new HashMap<>();
//
//        resultMap.put("status", SUCCESS);
//        resultMap.put("msg", String.format("%s으로 검색된 앱 반환 성공!", appSearchDTO.name()));
////        resultMap.put("response", appService.getAppByName(appSearchDTO));
//
//        return ResponseEntity.ok(resultMap);
//    }

    @PutMapping
    @Operation(summary = "앱 수정", description = "앱 ID로 앱 수정후, 앱 객체 반환하는 API")
    public ResponseEntity<Map<String, Object>> updateApp(
            @RequestBody AppUpdateDTO appUpdateDTO
    ) {
        Map<String, Object> resultMap = new HashMap<>();

        resultMap.put("status", SUCCESS);
        resultMap.put("msg", String.format("앱 %s 수정 성공!", appUpdateDTO.app_id()));
        resultMap.put("response", appService.updateApp(appUpdateDTO));

        return ResponseEntity.ok(resultMap);
    }

    @DeleteMapping("/{app_id}")
    @Operation(summary = "앱 삭제", description = "앱 ID로 앱 삭제하는 API, 앱 ID가 없는거여도 삭제되었다고 나옴.")
    public ResponseEntity<Map<String, Object>> deleteApp(
            @PathVariable String app_id
    ) {
        Map<String, Object> resultMap = new HashMap<>();

        resultMap.put("status", SUCCESS);
        resultMap.put("msg", String.format("앱 %s 삭제 성공!", app_id));
        resultMap.put("response", appService.deleteApp(app_id));

        return ResponseEntity.ok(resultMap);
    }
}
