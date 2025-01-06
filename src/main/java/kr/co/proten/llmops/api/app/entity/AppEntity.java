package kr.co.proten.llmops.api.app.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import kr.co.proten.llmops.api.workspace.entity.WorkspaceEntity;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "apps")
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AppEntity {

    @Id
    @Column(name = "app_id")
    private String appId;

    @ManyToOne(cascade = CascadeType.REMOVE, fetch = FetchType.LAZY)
    @JoinColumn(name = "workspace_id")
    private WorkspaceEntity workspace;

    @OneToOne(cascade = CascadeType.REMOVE, fetch = FetchType.LAZY)
    @JoinColumn(name = "workflow_id")
    private WorkflowEntity workflow;

    @NotNull
    @Column(nullable = false)
    private String name;

    private String description;

    @NotNull
    @Column(nullable = false)
    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;

    @NotNull
    @Column(nullable = false)
    private boolean isActive;
}
