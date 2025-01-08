package kr.co.proten.llmops.api.app.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import kr.co.proten.llmops.core.helpers.JsonConverter;

import java.time.LocalDateTime;
import java.util.Map;

@Entity
@Table(name = "workflows")
public class WorkflowEntity {

    @Id
    @Column(name = "workflow_id")
    private String workflowId;

    @NotNull
    @Column(nullable = false)
    private String name;

    @Column(name = "graph", columnDefinition = "jsonb")
    @Convert(converter = JsonConverter.class)
    private Map<String, Object> graph;

    @NotNull
    @Column(nullable = false)
    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;

    @NotNull
    @Column(nullable = false)
    private boolean isActive;
}
