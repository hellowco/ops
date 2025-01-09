package kr.co.proten.llmops.api.workflow.controller;

import kr.co.proten.llmops.api.workflow.dto.WorkflowDto;
import kr.co.proten.llmops.api.workflow.service.WorkflowService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/workflows")
public class WorkflowController {

    private final WorkflowService workflowService;

    public WorkflowController(WorkflowService workflowService) {
        this.workflowService = workflowService;
    }

    @PostMapping
    public ResponseEntity<WorkflowDto> createWorkflow(@RequestBody WorkflowDto workflowDto) {
        WorkflowDto createdWorkflow = workflowService.createWorkflow(workflowDto);
        return ResponseEntity.ok(createdWorkflow);
    }

    @GetMapping("/{id}")
    public ResponseEntity<WorkflowDto> getWorkflowById(@PathVariable String id) {
        return workflowService.getWorkflowById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping
    public ResponseEntity<List<WorkflowDto>> getAllWorkflows() {
        return ResponseEntity.ok(workflowService.getAllWorkflows());
    }

    @PutMapping("/{id}")
    public ResponseEntity<WorkflowDto> updateWorkflow(
            @PathVariable String id,
            @RequestBody WorkflowDto workflowDto) {
        return ResponseEntity.ok(workflowService.updateWorkflow(id, workflowDto));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteWorkflow(@PathVariable String id) {
        workflowService.deleteWorkflow(id);
        return ResponseEntity.noContent().build();
    }
}
