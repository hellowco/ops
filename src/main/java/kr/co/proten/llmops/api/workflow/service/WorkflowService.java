package kr.co.proten.llmops.api.workflow.service;

import kr.co.proten.llmops.api.workflow.dto.request.WorkflowUpdateDTO;
import kr.co.proten.llmops.api.workflow.dto.response.WorkflowResponseDTO;
import kr.co.proten.llmops.api.workflow.entity.WorkflowEntity;

public interface WorkflowService {

    WorkflowEntity createWorkflow();

    WorkflowEntity saveWorkflow(WorkflowEntity workflow);

    WorkflowResponseDTO getWorkflowById(String workflowId) throws Exception;

    WorkflowResponseDTO updateWorkflow(WorkflowUpdateDTO workflowDto);

    void executeWorkflow(String workflowId);
}
