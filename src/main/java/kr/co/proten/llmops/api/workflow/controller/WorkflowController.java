package kr.co.proten.llmops.api.workflow.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import kr.co.proten.llmops.api.node.dto.NodeResponse;
import kr.co.proten.llmops.api.workflow.dto.request.WorkflowUpdateDTO;
import kr.co.proten.llmops.api.workflow.service.WorkflowService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.codec.ServerSentEvent;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.access.prepost.PreAuthorize;
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
    @PreAuthorize("hasRole('ADMIN') or @workspaceSecurity.isUserInWorkspace(#id)")
    @Operation(summary = "하나의 워크플로우 반환", description = "워크플로우 ID로 워크플로우 객체 반환하는 API")
    public ResponseEntity<Map<String, Object>> getWorkflowById(@PathVariable String id) throws Exception {
        Map<String, Object> resultMap = new HashMap<>();

        resultMap.put("status", SUCCESS);
        resultMap.put("msg", String.format("워크플로우 %s 반환 성공!", id));
        resultMap.put("response", workflowService.getWorkflowById(id));

        return ResponseEntity.ok(resultMap);
    }

    @PutMapping
    @PreAuthorize("hasRole('ADMIN') or @workspaceSecurity.isUserInWorkspace(#workflowDto.workflow_id())")
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
            @RequestParam(defaultValue = "test") String query,
            @RequestHeader(value = "X-Custom-Auth", required = false) String customAuthHeader
    ) {
        // 요청 시점에 SecurityContextHolder로부터 인증 정보를 가져와 검증합니다.
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        log.info("Authentication: {}", authentication);

        boolean isCustomAuthValid = false;
        if(customAuthHeader != null) {
            // customAuthHeader가 존재하면, 이 값을 통해 별도 검증 진행
            // customAuthHeader는 RAG 화면에서 header로 별도로 지정
            isCustomAuthValid = "rag-service-endpoint".equals(customAuthHeader);
        }

        // 기본 인증 또는 커스텀 인증 둘 중 하나라도 유효하면 통과시키도록 함
        if ((authentication == null
             || !authentication.isAuthenticated()
             || authentication instanceof AnonymousAuthenticationToken)
            && !isCustomAuthValid) {
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
