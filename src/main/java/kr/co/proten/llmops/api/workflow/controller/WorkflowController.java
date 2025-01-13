package kr.co.proten.llmops.api.workflow.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import kr.co.proten.llmops.api.workflow.dto.request.WorkflowUpdateDTO;
import kr.co.proten.llmops.api.workflow.service.WorkflowService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@Tag(name = "Workflow", description = "워크플로우 조회, 수정하는 API")
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

}
