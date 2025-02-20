package kr.co.proten.llmops.api.workspace.service;

import kr.co.proten.llmops.api.workspace.dto.request.CustomPatchOperation;
import kr.co.proten.llmops.api.workspace.dto.request.WorkspaceRequestDTO;
import kr.co.proten.llmops.api.workspace.entity.Workspace;

import java.util.List;
import java.util.Map;
import java.util.Optional;

public interface WorkspaceService {
    Optional<Workspace> findWorkspaceById(String id);

    Map<String, Object> saveWorkspace(WorkspaceRequestDTO workspace);

    Map<String, Object> getWorkspaceById(String id);

    Map<String, Object> getAllWorkspaces(int page, int size, String sortField, String sortBy);

    Map<String, Object> getWorkspacesByName(int page, int size, String sortField, String sortBy, String keyword);

    Map<String, Object> updateWorkspace(String id, List<CustomPatchOperation> patchOperations);

    Map<String, Object> deleteWorkspace(String id);
}
