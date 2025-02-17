package kr.co.proten.llmops.api.workspace.mapper;

import kr.co.proten.llmops.api.workflow.entity.Workflow;
import kr.co.proten.llmops.api.workspace.dto.request.WorkspaceCreateDTO;
import kr.co.proten.llmops.api.workspace.dto.response.WorkspaceResponseDTO;
import kr.co.proten.llmops.api.workspace.entity.Workspace;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface WorkspaceMapper {
    Workspace requestToEntity(WorkspaceCreateDTO workspaceCreateDTO);
    WorkspaceResponseDTO entityToResponse(Workspace workspace);

    default String map(Workflow workflow) {
        return workflow != null ? workflow.getWorkflowId() : null;
    }
}
