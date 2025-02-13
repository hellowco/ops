package kr.co.proten.llmops.api.workspace.service;

import kr.co.proten.llmops.api.workspace.entity.Workspace;

import java.util.Optional;

public interface WorkspaceService {
    Optional<Workspace> findWorkspaceById(String id);

    Workspace saveWorkspace(Workspace workspace);
}
