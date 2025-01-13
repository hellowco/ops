package kr.co.proten.llmops.api.app.service.impl;

import kr.co.proten.llmops.api.app.dto.request.AppCreateDTO;
import kr.co.proten.llmops.api.app.dto.request.AppSearchDTO;
import kr.co.proten.llmops.api.app.dto.request.AppStateDTO;
import kr.co.proten.llmops.api.app.dto.request.AppUpdateDTO;
import kr.co.proten.llmops.api.app.dto.response.AppResponseDTO;
import kr.co.proten.llmops.api.app.entity.AppEntity;
import kr.co.proten.llmops.api.app.mapper.AppMapper;
import kr.co.proten.llmops.api.app.repository.AppRepository;
import kr.co.proten.llmops.api.app.service.AppService;
import kr.co.proten.llmops.api.workflow.entity.WorkflowEntity;
import kr.co.proten.llmops.api.workflow.service.WorkflowService;
import kr.co.proten.llmops.api.workspace.entity.WorkspaceEntity;
import kr.co.proten.llmops.api.workspace.service.WorkspaceService;
import kr.co.proten.llmops.core.exception.InvalidInputException;
import kr.co.proten.llmops.core.exception.StateChangeException;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.NoSuchElementException;

@Service
@Transactional
@RequiredArgsConstructor
public class AppServiceImpl implements AppService {

    private final Logger log = LoggerFactory.getLogger(this.getClass());
    private final AppRepository appRepository;
    private final AppMapper appMapper;
    private final WorkflowService workflowService;
    private final WorkspaceService workspaceService;

    @Override
    @Transactional
    public AppResponseDTO createApp(AppCreateDTO appCreateDTO) {
        try {
            WorkspaceEntity workspaceDummy = createDummyWorkspace(appCreateDTO.workspace_id());
            WorkspaceEntity savedWS = workspaceService.saveWorkspace(workspaceDummy);
            log.info("Workspace saved: {}", savedWS);

            WorkflowEntity workflow = workflowService.createWorkflow();
            WorkflowEntity savedWF = workflowService.saveWorkflow(workflow);
            log.info("Workflow saved: {}", savedWF);

            AppEntity appEntity = AppEntity.builder()
                    .workspace(workspaceDummy)
                    .workflow(workflow)
                    .name(appCreateDTO.name())
                    .description(appCreateDTO.description())
                    .build();

            AppResponseDTO responseDTO = appMapper.responseToDto(appRepository.save(appEntity));
            log.info("App saved: {}", appEntity);

            return responseDTO;
        } catch (Exception e) {
            log.error("Error occurred during app creation: {}", e.getMessage(), e);
            throw new InvalidInputException("앱 생성 실패!");
        }
    }

    @Override
    public AppResponseDTO getAppById(String workspaceId, String appId) {
        validateWorkspace(workspaceId);

        return appMapper.responseToDto(appRepository.findById(appId)
                .orElseThrow(() -> new NoSuchElementException(String.format("앱 ID [%s]에 해당하는 앱을 찾을 수 없습니다.", appId))));
    }

    @Override
    @Transactional(readOnly = true)
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
            throw new NoSuchElementException(String.format("앱 이름 [%s]에 해당하는 앱을 찾을 수 없습니다.", appSearchDTO.name()));
        }
    }

    @Override
    @Transactional(readOnly = true)
    public List<AppResponseDTO> getAllApps(String workspaceId, int page, int size, String sortField, String sortBy) {
        try {
            WorkspaceEntity workspace = validateWorkspace(workspaceId);

            sortBy = sortBy.toUpperCase();
            page = page < 1 ? 0 : page - 1;

            Pageable pageable = sortBy.equals("ASC")
                    ? PageRequest.of(page, size, Sort.by(Sort.Order.asc(sortField)))
                    : PageRequest.of(page, size, Sort.by(Sort.Order.desc(sortField)));

            log.info("find list results: {}", appRepository.findAllByWorkspace(workspace ,pageable));

            return appRepository.findAllByWorkspace(workspace ,pageable).stream()
                    .map(appMapper::responseToDto)
                    .toList();
        } catch (Exception e) {
            log.error("Error retrieving all apps: {}", e.getMessage(), e);
            throw new NoSuchElementException(String.format("워크스페이스 ID [%s]에 해당하는 앱을 찾을 수 없습니다.", workspaceId));
        }
    }

    @Override
    @Transactional
    public AppResponseDTO updateApp(AppUpdateDTO appUpdateDTO) {
        try {
            AppEntity existingApp = appRepository.findById(appUpdateDTO.app_id())
                    .orElseThrow(() -> new NoSuchElementException(String.format("수정할 앱 ID [%s]에 해당하는 앱을 찾을 수 없습니다.", appUpdateDTO.app_id())));

            existingApp.setName(appUpdateDTO.name());
            existingApp.setDescription(appUpdateDTO.description());

            return appMapper.responseToDto(appRepository.save(existingApp));
        } catch (Exception e) {
            log.error("Error updating app: {}", e.getMessage(), e);
            throw new StateChangeException("업데이트 변경 실패!");
        }
    }

    @Override
    public AppResponseDTO updateAppState(AppStateDTO appStateDTO) {
        try {
            AppEntity existingApp = appRepository.findById(appStateDTO.app_id())
                    .orElseThrow(() -> new NoSuchElementException(String.format("수정할 앱 ID [%s]에 해당하는 앱을 찾을 수 없습니다.", appStateDTO.app_id())));

            existingApp.setActive(appStateDTO.is_active());

            return appMapper.responseToDto(appRepository.save(existingApp));
        } catch (Exception e) {
            log.error("Error updating app state: {}", e.getMessage(), e);
            throw new StateChangeException("상태 변경 실패!");
        }
    }

    @Override
    public boolean deleteApp(String id) {
        if (!appRepository.existsById(id)) {
            throw new NoSuchElementException(String.format("삭제할 앱 ID [%s]에 해당하는 앱을 찾을 수 없습니다.", id));
        }
        appRepository.deleteById(id);
        return true;
    }

    @Override
    public boolean deleteAppList(List<String> appIdList) {
        try {
            appRepository.deleteAllById(appIdList);
            return true;
        } catch (Exception e) {
            throw new NoSuchElementException(String.format("삭제할 앱 ID [%s]에 해당하는 앱을 찾을 수 없습니다.", appIdList.toString()));
        }
    }

    private WorkspaceEntity validateWorkspace(String workspaceId) {
//        WorkspaceEntity workspaceDummy = createDummyWorkspace(workspaceId);
        WorkspaceEntity workspaceDummy = workspaceService.findWorkspaceById(workspaceId)
                        .orElseThrow(() -> new NoSuchElementException(String.format("워크스페이스 ID [%s]에 해당하는 워크스페이스을 찾을 수 없습니다.", workspaceId)));
        return workspaceDummy;
    }

    private WorkspaceEntity createDummyWorkspace(String workspaceId) {
        if (workspaceId == null) return null;
        return WorkspaceEntity
                .builder()
                .name("8ee589ef-c7bb-4f2a-a773-630abd0de8c7")
                .name("다락방")
                .description("테스트용 더미 데이터")
                .tokenLimit(50000)
                .build();
    }

}// end of class