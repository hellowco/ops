package kr.co.proten.llmops.api.workflow.service;

import kr.co.proten.llmops.api.node.dto.NodeResponse;
import kr.co.proten.llmops.api.workflow.dto.request.WorkflowUpdateDTO;
import kr.co.proten.llmops.api.workflow.dto.response.WorkflowResponseDTO;
import kr.co.proten.llmops.api.workflow.entity.Workflow;
import reactor.core.publisher.Flux;

import java.io.IOException;

public interface WorkflowService {

    Workflow createWorkflow() throws IOException;

    Workflow saveWorkflow(Workflow workflow);

    WorkflowResponseDTO getWorkflowById(String workflowId) throws Exception;

    WorkflowResponseDTO updateWorkflow(WorkflowUpdateDTO workflowDto);

    Flux<NodeResponse> executeWorkflow(String workflowId, String query);
}
