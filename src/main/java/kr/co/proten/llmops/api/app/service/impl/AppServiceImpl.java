package kr.co.proten.llmops.api.app.service.impl;

import kr.co.proten.llmops.api.app.dto.request.*;
import kr.co.proten.llmops.api.app.dto.response.AppResponseDTO;
import kr.co.proten.llmops.api.app.entity.AppEntity;
import kr.co.proten.llmops.api.app.mapper.AppMapper;
import kr.co.proten.llmops.api.app.repository.AppRepository;
import kr.co.proten.llmops.api.app.service.AppService;
import kr.co.proten.llmops.api.workflow.entity.WorkflowEntity;
import kr.co.proten.llmops.api.workflow.service.WorkflowService;
import kr.co.proten.llmops.api.workspace.entity.WorkspaceEntity;
import kr.co.proten.llmops.api.workspace.service.WorkspaceService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.NoSuchElementException;

import static kr.co.proten.llmops.core.helpers.DateUtil.generateCurrentTimestamp;
import static kr.co.proten.llmops.core.helpers.UUIDGenerator.generateUUID;

@Service
@Transactional
public class AppServiceImpl implements AppService {

    private final Logger log = LoggerFactory.getLogger(this.getClass());
    private final AppRepository appRepository;
    private final AppMapper appMapper;
    private final WorkflowService workflowService;
    private final WorkspaceService workspaceService;

    public AppServiceImpl(AppRepository appRepository, AppMapper appMapper, WorkflowService workflowService, WorkspaceService workspaceService) {
        this.appRepository = appRepository;
        this.appMapper = appMapper;
        this.workflowService = workflowService;
        this.workspaceService = workspaceService;
    }

    @Transactional
    @Override
    public AppResponseDTO createApp(AppCreateDTO appCreateDTO) {
        try {
            WorkspaceEntity workspaceDummy = createDummyWorkspace(appCreateDTO.workspace_id());
            WorkspaceEntity savedWS = workspaceService.saveWorkspace(workspaceDummy);
            log.info("Workspace saved: {}", savedWS);

            WorkflowEntity workflow = workflowService.createWorkflow();
            WorkflowEntity savedWF = workflowService.saveWorkflow(workflow);
            log.info("Workflow saved: {}", savedWF);

            AppEntity appEntity = AppEntity.builder()
                    .appId(generateUUID())
                    .workspace(workspaceDummy)
//                    .workflow(workflow)
                    .name(appCreateDTO.name())
                    .description(appCreateDTO.description())
                    .createdAt(generateCurrentTimestamp())
                    .updatedAt(null)
                    .isActive(true)
                    .build();

            AppResponseDTO responseDTO = appMapper.responseToDto(appRepository.save(appEntity));
            log.info("App saved: {}", appEntity);

            return responseDTO;
        } catch (Exception e) {
            log.error("Error occurred during app creation: {}", e.getMessage(), e);
        }
        return null;
    }

    @Override
    public AppResponseDTO getAppById(String workspaceId, String appId) {
        try {
            validateWorkspace(workspaceId);
            return appMapper.responseToDto(appRepository.findById(appId)
                    .orElseThrow(() -> new NoSuchElementException("App not found with id: " + appId)));
        } catch (Exception e) {
            log.error("Error retrieving app by ID: {}", e.getMessage(), e);
            throw e;
        }
    }

    @Override
    public List<AppResponseDTO> getAppByName(AppSearchDTO appSearchDTO) {
        try {
            validateWorkspace("8ee589ef-c7bb-4f2a-a773-630abd0de8c7");

            String sortBy = appSearchDTO.sort_by().toUpperCase();
            int page = appSearchDTO.page() < 1 ? 0 : appSearchDTO.page() - 1;

            Pageable pageable = sortBy.equals("ASC")
                    ? PageRequest.of(page, appSearchDTO.size(), Sort.by(Sort.Order.asc(appSearchDTO.sort_field())))
                    : PageRequest.of(page, appSearchDTO.size(), Sort.by(Sort.Order.desc(appSearchDTO.sort_field())));

            return appRepository.findByNameContaining(appSearchDTO.name(), pageable).stream()
                    .map(appMapper::responseToDto)
                    .toList();
        } catch (Exception e) {
            log.error("Error searching apps by name: {}", e.getMessage(), e);
            throw e;
        }
    }

    @Transactional(readOnly = true)
    @Override
    public List<AppResponseDTO> getAllApps(String workspaceId, int page, int size, String sortField, String sortBy) {
        try {
            WorkspaceEntity workspace = validateWorkspace(workspaceId);

            sortBy = sortBy.toUpperCase();
            page = page < 1 ? 0 : page - 1;

            Pageable pageable = sortBy.equals("ASC")
                    ? PageRequest.of(page, size, Sort.by(Sort.Order.asc(sortField)))
                    : PageRequest.of(page, size, Sort.by(Sort.Order.desc(sortField)));

//            return appRepository.findAll(pageable).stream()
//                    .map(appMapper::responseToDto)
//                    .toList();

            log.info("find list results: {}", appRepository.findAllByWorkspace(workspace));

            return  null;
        } catch (Exception e) {
            log.error("Error retrieving all apps: {}", e.getMessage(), e);
        }
        return List.of();
    }

    @Override
    public AppResponseDTO updateApp(AppUpdateDTO appUpdateDTO) {
        try {
            AppEntity existingApp = appRepository.findById(appUpdateDTO.app_id())
                    .orElseThrow(() -> new NoSuchElementException("App not found with id: " + appUpdateDTO.app_id()));

            existingApp.setName(appUpdateDTO.name());
            existingApp.setDescription(appUpdateDTO.description());
            existingApp.setUpdatedAt(generateCurrentTimestamp());

            return appMapper.responseToDto(appRepository.save(existingApp));
        } catch (Exception e) {
            log.error("Error updating app: {}", e.getMessage(), e);
            throw e;
        }
    }

    @Override
    public AppResponseDTO updateAppState(AppStateDTO appStateDTO) {
        try {
            AppEntity existingApp = appRepository.findById(appStateDTO.app_id())
                    .orElseThrow(() -> new NoSuchElementException("App not found with id: " + appStateDTO.app_id()));

            existingApp.setActive(appStateDTO.is_active());

            return appMapper.responseToDto(appRepository.save(existingApp));
        } catch (Exception e) {
            log.error("Error updating app state: {}", e.getMessage(), e);
            throw e;
        }
    }

    @Override
    public boolean deleteApp(String id) {
        try {
            appRepository.deleteById(id);
            return true;
        } catch (Exception e) {
            log.error("Error deleting app: {}", e.getMessage(), e);
            throw e;
        }
    }

    @Override
    public boolean deleteAppList(List<String> appIdList) {
        try {
            appRepository.deleteAllById(appIdList);
            return true;
        } catch (Exception e) {
            log.error("Error deleting app list: {}", e.getMessage(), e);
            throw e;
        }
    }

    private WorkspaceEntity validateWorkspace(String workspaceId) {
        WorkspaceEntity workspaceDummy = createDummyWorkspace(workspaceId);
        if (workspaceDummy == null) {
            throw new NoSuchElementException("Workspace not found with id: " + workspaceId);
        }
        return workspaceDummy;
    }

    private WorkspaceEntity createDummyWorkspace(String workspaceId) {
        if (workspaceId == null) return null;
        WorkspaceEntity workspace = new WorkspaceEntity();
        workspace.setWorkspaceId("8ee589ef-c7bb-4f2a-a773-630abd0de8c7");
        workspace.setName("다락방");
        workspace.setDescription("테스트용 더미 데이터");
        workspace.setCreatedAt(generateCurrentTimestamp());
        workspace.setUpdatedAt(generateCurrentTimestamp());
        workspace.setTokenLimit(50000);
        workspace.setActive(true);

        return workspace;
    }
}