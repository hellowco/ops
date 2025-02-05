package kr.co.proten.llmops.api.workflow.service;

import kr.co.proten.llmops.api.model.dto.response.ChatResponse;
import kr.co.proten.llmops.api.node.dto.NodeResponse;
import kr.co.proten.llmops.api.workflow.dto.request.WorkflowUpdateDTO;
import kr.co.proten.llmops.api.workflow.dto.response.WorkflowResponseDTO;
import kr.co.proten.llmops.api.workflow.entity.WorkflowEntity;
import reactor.core.publisher.Flux;

public interface WorkflowService {

    WorkflowEntity createWorkflow();

    WorkflowEntity saveWorkflow(WorkflowEntity workflow);

    WorkflowResponseDTO getWorkflowById(String workflowId) throws Exception;

    WorkflowResponseDTO updateWorkflow(WorkflowUpdateDTO workflowDto);

    Flux<NodeResponse> executeWorkflow(String workflowId, String query);
}
