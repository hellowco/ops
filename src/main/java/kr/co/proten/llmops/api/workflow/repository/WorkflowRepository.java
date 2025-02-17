package kr.co.proten.llmops.api.workflow.repository;

import kr.co.proten.llmops.api.workflow.entity.Workflow;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface WorkflowRepository extends JpaRepository<Workflow, String> {

    @Query(value = "SELECT graph->'graph'->>'nodes' FROM workflows WHERE workflow_id = :id", nativeQuery = true)
    String findNodesById(String id);

    @Query(value = "SELECT graph->'graph'->>'edges' FROM workflows WHERE workflow_id = :id", nativeQuery = true)
    String findEdgesById(String id);
}
