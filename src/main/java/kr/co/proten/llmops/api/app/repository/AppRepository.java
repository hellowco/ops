package kr.co.proten.llmops.api.app.repository;

import kr.co.proten.llmops.api.app.entity.AppEntity;
import kr.co.proten.llmops.api.workspace.entity.Workspace;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface AppRepository extends JpaRepository<AppEntity, String> {
    List<AppEntity> findByNameContainingAndWorkspace(String appName, Workspace workspace, Pageable pageable);

    List<AppEntity> findAllByWorkspace(Workspace workspace, Pageable pageable);

    Optional<AppEntity> findByWorkflow_WorkflowId(String workflowId);
}
