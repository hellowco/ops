package kr.co.proten.llmops.api.workspace.mapper;

import kr.co.proten.llmops.api.workflow.entity.Workflow;
import kr.co.proten.llmops.api.workspace.dto.request.WorkspaceRequestDTO;
import kr.co.proten.llmops.api.workspace.dto.response.WorkspaceResponseDTO;
import kr.co.proten.llmops.api.workspace.entity.Workspace;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface WorkspaceMapper {
    Workspace requestToEntity(WorkspaceRequestDTO workspaceRequestDTO);
    WorkspaceResponseDTO entityToResponse(Workspace workspace);

    default String map(Workflow workflow) {
        return workflow != null ? workflow.getWorkflowId() : null;
    }
}
