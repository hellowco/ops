package kr.co.proten.llmops.api.workspace.service.impl;

import kr.co.proten.llmops.api.workspace.entity.WorkspaceEntity;
import kr.co.proten.llmops.api.workspace.repository.WorkspaceRepository;
import kr.co.proten.llmops.api.workspace.service.WorkspaceService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class WorkspaceServiceImpl implements WorkspaceService {

    private final WorkspaceRepository workspaceRepository;

    public WorkspaceServiceImpl(WorkspaceRepository workspaceRepository) {
        this.workspaceRepository = workspaceRepository;
    }

    @Transactional
    @Override
    public WorkspaceEntity saveWorkspace(WorkspaceEntity workspace) {
        return workspaceRepository.save(workspace);
    }
}
