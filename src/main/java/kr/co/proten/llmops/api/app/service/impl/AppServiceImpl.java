package kr.co.proten.llmops.api.app.service.impl;

import kr.co.proten.llmops.api.app.dto.request.AppCreateDTO;
import kr.co.proten.llmops.api.app.dto.request.AppUpdateDTO;
import kr.co.proten.llmops.api.app.dto.response.AppResponseDTO;
import kr.co.proten.llmops.api.app.entity.AppEntity;
import kr.co.proten.llmops.api.app.mapper.AppMapper;
import kr.co.proten.llmops.api.app.repository.AppRepository;
import kr.co.proten.llmops.api.app.service.AppService;
import kr.co.proten.llmops.api.workspace.entity.WorkspaceEntity;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.NoSuchElementException;

import static kr.co.proten.llmops.core.helpers.DateUtil.generateCurrentTimestamp;
import static kr.co.proten.llmops.core.helpers.UUIDGenerator.generateUUID;
import static kr.co.proten.llmops.core.helpers.UUIDGenerator.generateUUID4Doc;

@Service
public class AppServiceImpl implements AppService {

    private final Logger log = LoggerFactory.getLogger(this.getClass());
    private final AppRepository appRepository;
    private final AppMapper appMapper;

    public AppServiceImpl(AppRepository appRepository, AppMapper appMapper) {
        this.appRepository = appRepository;
        this.appMapper = appMapper;
    }

    @Override
    public AppResponseDTO createApp(AppCreateDTO appCreateDTO) {
        //workspace_id로 ws 찾기
        WorkspaceEntity workspaceDummy = createDummyWorkspace(appCreateDTO.workspace_id());

        //workflow 생성

        //앱 생성
        AppEntity appEntity =
                AppEntity.builder()
                        .appId(generateUUID())
                        .workspace(workspaceDummy)
                        .workflow(null)
                        .name(appCreateDTO.name())
                        .description(appCreateDTO.description())
                        .createdAt(generateCurrentTimestamp())
                        .updatedAt(null)
                        .isActive(true)
                        .build();

        AppResponseDTO responseDTO = appMapper.responseToDto(appRepository.save(appEntity));
        log.debug("created app: {}", responseDTO);

        return responseDTO;
    }

    @Override
    public AppResponseDTO getAppById(String id) {
        return appMapper.responseToDto(appRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("App not found with id: " + id)));
    }

    @Override
    public List<AppResponseDTO> getAllApps(String workspaceId) {
        workspaceId = "8ee589ef-c7bb-4f2a-a773-630abd0de8c7";

        //워크스페이스 검증
        WorkspaceEntity workspaceDummy = createDummyWorkspace(workspaceId);
        if(workspaceDummy == null) {
            throw new NoSuchElementException("Workspace not found with id: " + workspaceId);
        }

        return appRepository.findAll().stream()
                .map(appMapper::responseToDto)
                .toList();
    }

    @Override
    public AppResponseDTO updateApp(AppUpdateDTO appUpdateDTO) {
        //저장된 app 찾아오기
        AppEntity existingApp = appRepository.findById(appUpdateDTO.app_id()).orElseThrow(() -> new NoSuchElementException("App not found with id: " + appUpdateDTO.app_id()));
        //업데이트 값으로 변경
        existingApp.setName(appUpdateDTO.name());
        existingApp.setDescription(appUpdateDTO.description());
        existingApp.setUpdatedAt(generateCurrentTimestamp());

        return appMapper.responseToDto(appRepository.save(existingApp));
    }

    @Override
    public boolean deleteApp(String id) {
        appRepository.deleteById(id);
        return true;
    }


    /**
     * 워크스페이스 더미 데이터 생성 메서드
     * TODO:: 워크스페이스 작업 후, 삭제해야할 메서드
     * @return workspace
     */
    private static WorkspaceEntity createDummyWorkspace(String workspaceId) {
        if(workspaceId == null) return null;
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
