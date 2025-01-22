package kr.co.proten.llmops.api.workspace.service;

import kr.co.proten.llmops.api.workspace.entity.WorkspaceEntity;

import java.util.Optional;

public interface WorkspaceService {
    Optional<WorkspaceEntity> findWorkspaceById(String id);

    WorkspaceEntity saveWorkspace(WorkspaceEntity workspace);
}
