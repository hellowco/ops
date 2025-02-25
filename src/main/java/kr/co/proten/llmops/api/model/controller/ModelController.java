package kr.co.proten.llmops.api.model.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import kr.co.proten.llmops.api.model.dto.request.ModelListRequest;
import kr.co.proten.llmops.api.model.dto.request.ModelRequest;
import kr.co.proten.llmops.api.model.dto.request.ModelUserRequest;
import kr.co.proten.llmops.api.model.dto.response.ChatResponse;
import kr.co.proten.llmops.api.model.service.ProviderService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;

import java.util.Map;

@Slf4j
@Tag(name = "Model", description = "Ollama, OpenAI의 모델을 사용하여 채팅하는 API")
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/model")
public class ModelController {

    private final ProviderService providerService;

    @Operation(
            summary = "Stream chat responses",
            description = "Streams chat responses using Server-Sent Events",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Successful streaming response",
                            content = @Content(
                                    mediaType = MediaType.TEXT_EVENT_STREAM_VALUE,
                                    schema = @Schema(implementation = ChatResponse.class)
                            )
                    ),
                    @ApiResponse(
                            responseCode = "400",
                            description = "Invalid request parameters"
                    ),
                    @ApiResponse(
                            responseCode = "500",
                            description = "Server error"
                    )
            }
    )
    @GetMapping(
            path = "/stream",
            produces = MediaType.TEXT_EVENT_STREAM_VALUE
    )
    public Flux<ChatResponse> processChat(@ModelAttribute ModelRequest request) {
        // 요청 시점에 SecurityContextHolder로부터 인증 정보를 가져와 검증합니다.
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        log.info("Authentication: {}", authentication);
        if (authentication == null
            || !authentication.isAuthenticated()
            || authentication instanceof AnonymousAuthenticationToken) {
            return Flux.error(new AccessDeniedException("Require an Authorization"));
        }

        return providerService.streamChat(request)
                .doOnComplete(() -> log.info("SSE 스트림 완료"));
    }

    @GetMapping("/providers")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "모델 제공자 리스트 반환 (관리자)", description = "모델 제공자 리스트 반환하는 API")
    public ResponseEntity<Map<String, Object>> getProviderList(
    ) {
        Map<String, Object> resultMap;

        resultMap = providerService.getProviderList();

        return ResponseEntity.ok().body(resultMap);
    }

    @PostMapping("/list")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "모델 제공자 및 모델 타입에 따른 리스트 반환 (관리자)", description = "모델 제공자와 타입으로 호환되는 모델 리스트 반환하는 API")
    public ResponseEntity<Map<String, Object>> modelList(
            @RequestBody ModelListRequest modelListRequest
    ) {
        // 모델 리스트 요청
        // 모델 제공자, 임베딩/검색
        Map<String, Object> resultMap;

        resultMap = providerService.getModelList(modelListRequest);

        return ResponseEntity.ok().body(resultMap);
    }

    @PostMapping("/save")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "일반 사용자가 사용가능한 모델 등록 (관리자)", description = "모델 제공자와 타입을 사용가능 모델 리스트에 등록하는 API")
    public ResponseEntity<Map<String, Object>> saveModel(
            @RequestBody ModelUserRequest modelUserRequest

    ) {
        Map<String, Object> resultMap;

        resultMap = providerService.saveModel(modelUserRequest);

        return ResponseEntity.ok().body(resultMap);
    }

    @DeleteMapping("/")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "일반 사용자가 사용 가능한 모델 삭제 (관리자)", description = "모델 제공자와 타입을 사용가능 모델 리스트에서 삭제하는 API")
    public ResponseEntity<Map<String, Object>> deleteModel(
            @RequestParam String modelId
    ) {
        Map<String, Object> resultMap;

        resultMap = providerService.deleteModel(modelId);

        return ResponseEntity.ok().body(resultMap);
    }

    @GetMapping("/list")
    @Operation(summary = "일반 사용자가 사용가능한 모델 리스트", description = "관리자가 등록한 사용가능한 모델 리스트 반환하는 API")
    public ResponseEntity<Map<String, Object>> getModelList(
            @RequestParam(value = "provider") String provider,
            @RequestParam(value = "modelType") String modelType
    ) {
        Map<String, Object> resultMap;

        resultMap = providerService.getAllModelList(provider, modelType);

        return ResponseEntity.ok().body(resultMap);
    }

    @GetMapping("/embed")
    @Operation(summary = "일반 사용자가 사용가능한 모델 리스트", description = "관리자가 등록한 사용가능한 모델 리스트 반환하는 API")
    public ResponseEntity<Map<String, Object>> getEmbedModelList(
    ) {
        Map<String, Object> resultMap;

        resultMap = providerService.getModelList("EMBED");

        return ResponseEntity.ok().body(resultMap);
    }

    @GetMapping("/search")
    @Operation(summary = "일반 사용자가 사용가능한 모델 리스트", description = "관리자가 등록한 사용가능한 모델 리스트 반환하는 API")
    public ResponseEntity<Map<String, Object>> getSearchModelList(
    ) {
        Map<String, Object> resultMap;

        resultMap = providerService.getModelList("SEARCH");

        return ResponseEntity.ok().body(resultMap);
    }
}
