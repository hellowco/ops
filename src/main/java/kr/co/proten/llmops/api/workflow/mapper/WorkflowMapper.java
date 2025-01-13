package kr.co.proten.llmops.api.workflow.mapper;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import kr.co.proten.llmops.api.workflow.dto.request.WorkflowUpdateDTO;
import kr.co.proten.llmops.api.workflow.dto.response.WorkflowResponseDTO;
import kr.co.proten.llmops.api.workflow.entity.WorkflowEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.Map;

@Mapper(componentModel = "spring")
public interface WorkflowMapper {

    // DTO -> Entity
    @Mapping(target = "graph", source = "graph")
    WorkflowEntity toEntity(WorkflowUpdateDTO dto);

    // Entity -> DTO
    @Mapping(target = "graph", source = "graph")
    WorkflowResponseDTO toDto(WorkflowEntity entity);

    // Custom mapping methods
    @SuppressWarnings("unchecked")
    default Map<String, Object> mapStringToMap(String value) {
        if (value == null) {
            return null;
        }
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            return objectMapper.readValue(value, Map.class);
        } catch (JsonProcessingException e) {
            throw new RuntimeException("Failed to convert String to Map", e);
        }
    }

    default String mapMapToString(Map<String, Object> map) {
        if (map == null) {
            return null;
        }
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            return objectMapper.writeValueAsString(map);
        } catch (JsonProcessingException e) {
            throw new RuntimeException("Failed to convert Map to String", e);
        }
    }
}
