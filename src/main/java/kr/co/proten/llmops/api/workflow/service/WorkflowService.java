package kr.co.proten.llmops.api.workflow.service;

import kr.co.proten.llmops.api.workflow.dto.WorkflowDto;

import java.util.List;
import java.util.Optional;

public interface WorkflowService {

    WorkflowDto createWorkflow(WorkflowDto workflowDto);

    Optional<WorkflowDto> getWorkflowById(String workflowId);

    List<WorkflowDto> getAllWorkflows();

    WorkflowDto updateWorkflow(String workflowId, WorkflowDto workflowDto);

    void deleteWorkflow(String workflowId);
}
