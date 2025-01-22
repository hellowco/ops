package kr.co.proten.llmops.api.app.mapper;

import kr.co.proten.llmops.api.app.dto.request.AppCreateDTO;
import kr.co.proten.llmops.api.app.dto.request.AppUpdateDTO;
import kr.co.proten.llmops.api.app.dto.response.AppResponseDTO;
import kr.co.proten.llmops.api.app.entity.AppEntity;
import kr.co.proten.llmops.api.workflow.entity.WorkflowEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface AppMapper {
    @Mapping(target = "workflowId", source = "workflow")
    AppResponseDTO responseToDto(AppEntity entity);
    AppEntity createToEntity(AppCreateDTO dto);
    AppEntity updateToEntity(AppUpdateDTO dto);

    default String map(WorkflowEntity workflowEntity) {
        return workflowEntity != null ? workflowEntity.getWorkflowId() : null;
    }
}
