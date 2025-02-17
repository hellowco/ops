package kr.co.proten.llmops.api.workspace.repository;

import kr.co.proten.llmops.api.workspace.entity.Workspace;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface WorkspaceRepository extends JpaRepository<Workspace, String> {
    Optional<Workspace> findByName(String name);

    Page<Workspace> findByNameContainingIgnoreCase(String keyword, Pageable pageable);
}
