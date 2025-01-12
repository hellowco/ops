package kr.co.proten.llmops.api.workspace.service;

import kr.co.proten.llmops.api.workspace.entity.WorkspaceEntity;

public interface WorkspaceService {
    WorkspaceEntity saveWorkspace(WorkspaceEntity workspace);
}
