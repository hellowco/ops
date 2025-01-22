package kr.co.proten.llmops.api.workspace.repository;

import kr.co.proten.llmops.api.workspace.entity.WorkspaceEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface WorkspaceRepository extends JpaRepository<WorkspaceEntity, String> {

}
