package kr.co.proten.llmops.api.workflow.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import kr.co.proten.llmops.core.helpers.JsonConverter;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.time.LocalDateTime;
import java.util.Map;

@Entity
@Builder
@AllArgsConstructor
@NoArgsConstructor
@ToString
@Table(name = "workflows")
public class WorkflowEntity {

    @Id
    @Column(name = "workflow_id")
    private String workflowId;

    @NotNull
    @Column(name = "graph", columnDefinition = "jsonb", nullable = false)
//    @Convert(converter = JsonConverter.class)
    private String graph;

    @NotNull
    @Column(nullable = false)
    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;

    @NotNull
    @Column(nullable = false)
    private boolean isActive;
}
