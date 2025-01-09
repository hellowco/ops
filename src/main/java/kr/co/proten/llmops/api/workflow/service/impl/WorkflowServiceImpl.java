package kr.co.proten.llmops.api.workflow.service.impl;

import kr.co.proten.llmops.api.workflow.dto.WorkflowDto;
import kr.co.proten.llmops.api.workflow.entity.WorkflowEntity;
import kr.co.proten.llmops.api.workflow.repository.WorkflowRepository;
import kr.co.proten.llmops.api.workflow.service.WorkflowService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class WorkflowServiceImpl implements WorkflowService {

    private final WorkflowRepository workflowRepository;

    public WorkflowServiceImpl(WorkflowRepository workflowRepository) {
        this.workflowRepository = workflowRepository;
    }

    @Transactional
    @Override
    public WorkflowDto createWorkflow(WorkflowDto workflowDto) {
//        WorkflowEntity entity = mapToEntity(workflowDto);
//        entity.setCreatedAt(LocalDateTime.now());
//        WorkflowEntity savedEntity = workflowRepository.save(entity);
//        return mapToDto(savedEntity);
        return null;
    }

    @Override
    public Optional<WorkflowDto> getWorkflowById(String workflowId) {
        workflowId = "1f4d36ef-6d72-4e3a-a739-625d34c6d306";
        //List<FlowNode> nodeList = workflowRepository.findNodesById(workflowId);
        //List<FlowEdge> edgeList = workflowRepository.findEdgesById(workflowId);

        return null;
        //return workflowRepository.findById(workflowId).map(this::mapToDto);
    }

    @Override
    public List<WorkflowDto> getAllWorkflows() {
        return null;
//        return workflowRepository.findAll().stream()
//                .map(this::mapToDto)
//                .toList();
    }

    @Transactional
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

    @Transactional
    @Override
    public void deleteWorkflow(String workflowId) {
        if (!workflowRepository.existsById(workflowId)) {
            throw new RuntimeException("Workflow not found");
        }
        workflowRepository.deleteById(workflowId);
    }
}
