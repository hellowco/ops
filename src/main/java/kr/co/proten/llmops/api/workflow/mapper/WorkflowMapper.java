package kr.co.proten.llmops.api.workflow.mapper;

import kr.co.proten.llmops.api.workflow.dto.WorkflowDto;
import kr.co.proten.llmops.api.workflow.entity.WorkflowEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface WorkflowMapper {

    WorkflowDto toDto(WorkflowEntity entity);

    WorkflowEntity toEntity(WorkflowDto dto);

    @Mapping(target = "createdAt", expression = "java(java.time.LocalDateTime.now())")
    WorkflowEntity toEntityForCreate(WorkflowDto dto);

    @Mapping(target = "updatedAt", expression = "java(java.time.LocalDateTime.now())")
    WorkflowEntity updateEntityFromDto(WorkflowDto dto, @MappingTarget WorkflowEntity entity);
}
