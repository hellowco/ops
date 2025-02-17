package kr.co.proten.llmops.api.workspace.service;

import kr.co.proten.llmops.api.workspace.dto.request.WorkspaceCreateDTO;
import kr.co.proten.llmops.api.workspace.entity.Workspace;
import org.springframework.transaction.annotation.Transactional;

import java.util.Map;
import java.util.Optional;

public interface WorkspaceService {
    Optional<Workspace> findWorkspaceById(String id);

    Map<String, Object> saveWorkspace(WorkspaceCreateDTO workspace);

    Map<String, Object> getWorkspaceById(String id);

    Map<String, Object> getAllWorkspaces(int page, int size, String sortField, String sortBy);

    Map<String, Object> getWorkspacesByName(int page, int size, String sortField, String sortBy, String keyword);

    Map<String, Object> deleteWorkspace(String id);
}
