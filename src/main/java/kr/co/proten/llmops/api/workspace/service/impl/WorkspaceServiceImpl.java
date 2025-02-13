package kr.co.proten.llmops.api.workspace.service.impl;

import kr.co.proten.llmops.api.workspace.entity.Workspace;
import kr.co.proten.llmops.api.workspace.repository.WorkspaceRepository;
import kr.co.proten.llmops.api.workspace.service.WorkspaceService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@Transactional
public class WorkspaceServiceImpl implements WorkspaceService {

    private final WorkspaceRepository workspaceRepository;

    public WorkspaceServiceImpl(WorkspaceRepository workspaceRepository) {
        this.workspaceRepository = workspaceRepository;
    }


    @Override
    @Transactional(readOnly = true)
    public Optional<Workspace> findWorkspaceById(String id){
        return workspaceRepository.findById(id);
    }

    @Override
    @Transactional
    public Workspace saveWorkspace(Workspace workspace) {
        return workspaceRepository.save(workspace);
    }
}
