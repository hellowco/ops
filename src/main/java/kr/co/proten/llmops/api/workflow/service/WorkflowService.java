package kr.co.proten.llmops.api.workflow.service;

import kr.co.proten.llmops.api.workflow.dto.WorkflowDto;
import kr.co.proten.llmops.api.workflow.entity.WorkflowEntity;

import java.util.List;
import java.util.Optional;

public interface WorkflowService {

    WorkflowEntity createWorkflow();

    WorkflowDto saveWorkflow(WorkflowDto workflow);

    WorkflowEntity saveWorkflow(WorkflowEntity workflow);

    Optional<WorkflowDto> getWorkflowById(String workflowId) throws Exception;

    List<WorkflowDto> getAllWorkflows();

    WorkflowDto updateWorkflow(String workflowId, WorkflowDto workflowDto);

    void deleteWorkflow(String workflowId);
}
