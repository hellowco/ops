package kr.co.proten.llmops.api.workflow.service.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import kr.co.proten.llmops.api.workflow.dto.FlowEdge;
import kr.co.proten.llmops.api.workflow.dto.FlowNode;
import kr.co.proten.llmops.api.workflow.dto.request.WorkflowUpdateDTO;
import kr.co.proten.llmops.api.workflow.dto.response.WorkflowResponseDTO;
import kr.co.proten.llmops.api.workflow.entity.WorkflowEntity;
import kr.co.proten.llmops.api.workflow.mapper.WorkflowMapper;
import kr.co.proten.llmops.api.workflow.repository.WorkflowRepository;
import kr.co.proten.llmops.api.workflow.service.WorkflowService;
import kr.co.proten.llmops.core.exception.InvalidInputException;
import kr.co.proten.llmops.core.helpers.MappingLoader;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;

@Service
@Transactional
@RequiredArgsConstructor
public class WorkflowServiceImpl implements WorkflowService {

    private final Logger log = LoggerFactory.getLogger(this.getClass());
    private final ObjectMapper objectMapper = new ObjectMapper();
    private final WorkflowRepository workflowRepository;
    private final WorkflowMapper workflowMapper;

    @Override
    public WorkflowEntity createWorkflow() {

        return WorkflowEntity.builder()
                .graph(getDefaultWorkflow())
                .build();
    }

    /**
     * resourcePath에 저장된 json 파일로 초기 워크플로우 만드는 메서드
     * @return Map으로 변환된 초기 워크플로우
     */
    private Map<String, Object> getDefaultWorkflow() {
        String resourcePath = "mappings/DefaultWorkflow.json";
        Map<String, Object> result;

        try (InputStream inputStream = MappingLoader.class.getClassLoader().getResourceAsStream(resourcePath)) {
            if (inputStream == null) {
                throw new IOException("Resource not found: " + resourcePath);
            }

            ObjectMapper objectMapper = new ObjectMapper();
            // JSON 데이터를 Map으로 변환
            result = objectMapper.readValue(inputStream, new TypeReference<>() {});
        } catch (IOException e) {
            throw new RuntimeException("Failed to load resource: " + resourcePath, e);
        }

        return result;
    }

    @Override
    @Transactional
    public WorkflowEntity saveWorkflow(WorkflowEntity workflow) {
        return workflowRepository.save(workflow);
    }

    @Override
    public WorkflowResponseDTO updateWorkflow(WorkflowUpdateDTO workflowDto) {
        WorkflowEntity existingEntity = workflowRepository.findById(workflowDto.workflow_id())
                .orElseThrow(() -> new NoSuchElementException(String.format("워크플로우 ID [%s]에 해당하는 워크플로우를 찾을 수 없습니다.", workflowDto.workflow_id())));

        Map<String,Object> mapGraph;

        ObjectMapper objectMapper = new ObjectMapper();
        // JSON 데이터를 Map으로 변환
        try {
            mapGraph = objectMapper.readValue(workflowDto.graph(), new TypeReference<>() {});
        } catch (JsonProcessingException e) {
            throw new InvalidInputException("워크플로우 JSON을 다시 확인해주세요.");
        }

        existingEntity.setGraph(mapGraph); // string -> map 변환

        WorkflowEntity updatedEntity = workflowRepository.save(existingEntity);
        return workflowMapper.toDto(updatedEntity);
    }

    @Override
    public WorkflowResponseDTO getWorkflowById(String workflowId) {
        WorkflowEntity workflowEntity = workflowRepository.findById(workflowId)
                .orElseThrow(() -> new NoSuchElementException(String.format("워크플로우 ID [%s]에 해당하는 워크플로우를 찾을 수 없습니다.", workflowId)));

        return workflowMapper.toDto(workflowEntity);
    }

    @Override
    public void executeWorkflow(String workflowId) {
//        List<FlowNode> nodeList = getNodes(workflowId);
//        List<FlowEdge> edgeList = getEdges(workflowId);
        log.info("nodeList = {}", findNodesById(workflowId));
        log.info("edgeList = {}", findEdgesById(workflowId));
    }

    /**
     * 워크플로우의 id로 graph의 nodes를 찾아옴.
     * @param id 워크플로우의 id
     * @return FlowNode 객체의 리스트
     */
    private List<FlowNode> findNodesById(String id) {
        String nodesJson = workflowRepository.findNodesById(id);

        if (nodesJson == null || nodesJson.isEmpty()) {
            throw new RuntimeException("No nodes found for workflow ID: " + id);
        }

        try {
            return objectMapper.readValue(nodesJson, new TypeReference<>() {});
        } catch (Exception e) {
            throw new RuntimeException("Error converting JSON to List<FlowNode>", e);
        }
    }

    /**
     * 워크플로우의 id로 graph의 edges를 찾아옴.
     * @param id 워크플로우의 id
     * @return FlowEdge 객체의 리스트
     */
    private List<FlowEdge> findEdgesById(String id) {
        String edgesJson = workflowRepository.findEdgesById(id);

        if (edgesJson == null || edgesJson.isEmpty()) {
            throw new RuntimeException("No edges found for workflow ID: " + id);
        }

        try {
            return objectMapper.readValue(edgesJson, new TypeReference<>() {});
        } catch (Exception e) {
            throw new RuntimeException("Error converting JSON to List<FlowEdge>", e);
        }
    }

} // end of class
