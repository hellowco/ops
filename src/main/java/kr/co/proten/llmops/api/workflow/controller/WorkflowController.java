package kr.co.proten.llmops.api.workflow.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import kr.co.proten.llmops.api.model.dto.response.ChatResponse;
import kr.co.proten.llmops.api.node.dto.NodeResponse;
import kr.co.proten.llmops.api.workflow.dto.request.WorkflowUpdateDTO;
import kr.co.proten.llmops.api.workflow.service.WorkflowService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.codec.ServerSentEvent;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;

import java.util.HashMap;
import java.util.Map;

@Tag(name = "Workflow", description = "워크플로우 조회, 수정하는 API")
@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/workflows")
public class WorkflowController {

    private static final String SUCCESS = "success";
    private final WorkflowService workflowService;

    @GetMapping("/{id}")
    @Operation(summary = "하나의 워크플로우 반환", description = "워크플로우 ID로 워크플로우 객체 반환하는 API")
    public ResponseEntity<Map<String, Object>> getWorkflowById(@PathVariable String id) throws Exception {
        Map<String, Object> resultMap = new HashMap<>();

        resultMap.put("status", SUCCESS);
        resultMap.put("msg", String.format("워크플로우 %s 반환 성공!", id));
        resultMap.put("response", workflowService.getWorkflowById(id));

        return ResponseEntity.ok(resultMap);
    }

    @PutMapping
    @Operation(summary = "워크플로우 수정", description = "워크플로우 ID로 워크플로우 수정후, 워크플로우 객체 반환하는 API")
    public ResponseEntity<Map<String, Object>> updateWorkflow (
            @RequestBody WorkflowUpdateDTO workflowDto) {
        Map<String, Object> resultMap = new HashMap<>();

        resultMap.put("status", SUCCESS);
        resultMap.put("msg", String.format("워크플로우 %s 수정 성공!", workflowDto.workflow_id()));
        resultMap.put("response", workflowService.updateWorkflow(workflowDto));

        return ResponseEntity.ok(resultMap);
    }

    @GetMapping(path = "/execute", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    @Operation(summary = "워크플로우 실행", description = "워크플로우 ID로 워크플로우(그래프) 실행")
    public Flux<ServerSentEvent<NodeResponse>> executeWorkflow (
            @RequestParam(defaultValue = "658ffad3-59a8-40dd-9623-c7302d4cc044") String workflowId,
            @RequestParam(defaultValue = "test") String query
    ) {
        // 요청 시점에 SecurityContextHolder로부터 인증 정보를 가져와 검증합니다.
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        log.info("Authentication: {}", authentication);
        if (authentication == null
            || !authentication.isAuthenticated()
            || authentication instanceof AnonymousAuthenticationToken) {
            return Flux.error(new AccessDeniedException("Require an Authorization"));
        }

        Flux<NodeResponse> nodeResponseFlux =  workflowService.executeWorkflow(workflowId, query);

        // NodeResponse를 ServerSentEvent로 변환
        return nodeResponseFlux.map(response ->
                        ServerSentEvent.<NodeResponse>builder()
                                .data(response)              // 실제 데이터
                                .build()
                )
                .onErrorResume(e -> {
                    log.error("node exception: {}", e.getMessage());
                    return Flux.just(
                            ServerSentEvent.<NodeResponse>builder()
                                    .event("nodeResponse-error")
                                    .id("error-event")
                                    .build()
                    );
                });
    }

}
