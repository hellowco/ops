package kr.co.proten.llmops.api.workflow.service.impl;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import kr.co.proten.llmops.api.workflow.dto.FlowEdge;
import kr.co.proten.llmops.api.workflow.dto.FlowNode;
import kr.co.proten.llmops.api.workflow.dto.WorkflowDto;
import kr.co.proten.llmops.api.workflow.entity.WorkflowEntity;
import kr.co.proten.llmops.api.workflow.repository.WorkflowRepository;
import kr.co.proten.llmops.api.workflow.service.WorkflowService;
import kr.co.proten.llmops.core.helpers.MappingLoader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static kr.co.proten.llmops.core.helpers.DateUtil.generateCurrentTimestamp;
import static kr.co.proten.llmops.core.helpers.UUIDGenerator.generateUUID;

@Service
@Transactional
public class WorkflowServiceImpl implements WorkflowService {

    private final Logger log = LoggerFactory.getLogger(this.getClass());
    private final WorkflowRepository workflowRepository;
    private final ObjectMapper objectMapper = new ObjectMapper();

    public WorkflowServiceImpl(WorkflowRepository workflowRepository) {
        this.workflowRepository = workflowRepository;
    }

    @Override
    public WorkflowEntity createWorkflow() {

        return WorkflowEntity.builder()
                .workflowId(generateUUID())
                .graph(getDefaultWorkflow().toString())
                .createdAt(generateCurrentTimestamp())
                .isActive(true)
                .build();
    }

    private Map<String, Object> getDefaultWorkflow() {
        String resourcePath = "mappings/DefaultWorkflow.json";
        Map<String, Object> result;

        try (InputStream inputStream = MappingLoader.class.getClassLoader().getResourceAsStream(resourcePath)) {
            if (inputStream == null) {
                throw new IOException("Resource not found: " + resourcePath);
            }

            ObjectMapper objectMapper = new ObjectMapper();
            // JSON 데이터를 Map으로 변환
            result = objectMapper.readValue(inputStream, new TypeReference<Map<String, Object>>() {});
        } catch (IOException e) {
            throw new RuntimeException("Failed to load resource: " + resourcePath, e);
        }

        return result;
    }

    @Override
    public WorkflowDto saveWorkflow(WorkflowDto workflow) {
        return null;
    }

    @Transactional
    @Override
    public WorkflowEntity saveWorkflow(WorkflowEntity workflow) {
        log.info("To be Saved workflow: {}", workflow);
        WorkflowEntity saved = workflowRepository.save(workflow);
        log.info("Saved workflow: {}", saved);
        return saved;
    }

    @Override
    public Optional<WorkflowDto> getWorkflowById(String workflowId) throws Exception {
        workflowId = "1f4d36ef-6d72-4e3a-a739-625d34c6d306";
        //log.info(workflowRepository.findNodesById(workflowId));
        //log.info(workflowRepository.findEdgesById(workflowId));
        List<FlowNode> nodeList = getNodes(workflowId);
        List<FlowEdge> edgeList = getEdges(workflowId);
        log.info("nodeList = {}", nodeList);
        log.info("edgeList = {}", edgeList);

        return Optional.empty();
        //return workflowRepository.findById(workflowId).map(this::mapToDto);
    }

    @Override
    public List<WorkflowDto> getAllWorkflows() {
        return null;
//        return workflowRepository.findAll().stream()
//                .map(this::mapToDto)
//                .toList();
    }

    @Override
    public WorkflowDto updateWorkflow(String workflowId, WorkflowDto workflowDto) {
        WorkflowEntity existingEntity = workflowRepository.findById(workflowId)
                .orElseThrow(() -> new RuntimeException("Workflow not found"));
        
//        existingEntity.setName(workflowDto.getName());
//        existingEntity.setGraph(workflowDto.getGraph());
//        existingEntity.setIsActive(workflowDto.isActive());
//        existingEntity.setUpdatedAt(LocalDateTime.now());
//
//        WorkflowEntity updatedEntity = workflowRepository.save(existingEntity);
//        return mapToDto(updatedEntity);
        return null;
    }

    @Override
    public void deleteWorkflow(String workflowId) {
        if (!workflowRepository.existsById(workflowId)) {
            throw new RuntimeException("Workflow not found");
        }
        workflowRepository.deleteById(workflowId);
    }

    public List<FlowNode> getNodes(String workflowId) throws Exception {
        String nodesJson = workflowRepository.findNodesById(workflowId);
        return objectMapper.readValue(nodesJson, new TypeReference<List<FlowNode>>() {});
    }

    public List<FlowEdge> getEdges(String workflowId) throws Exception {
        String edgesJson = workflowRepository.findEdgesById(workflowId);
        return objectMapper.readValue(edgesJson, new TypeReference<List<FlowEdge>>() {});
    }
}
