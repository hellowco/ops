package kr.co.proten.llmops.api.app.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import kr.co.proten.llmops.api.workflow.entity.Workflow;
import kr.co.proten.llmops.api.workspace.entity.Workspace;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

import static kr.co.proten.llmops.core.helpers.UUIDGenerator.generateUUID;

@Entity
@Table(name = "apps")
@Builder
@ToString(exclude = {"workspace", "workflow"})
@Getter
@NoArgsConstructor
@AllArgsConstructor
@EntityListeners(AuditingEntityListener.class)
public class AppEntity {

    @Id
    @Column(name = "app_id")
    private String appId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "workspace_id")
    private Workspace workspace;

    @OneToOne(cascade = CascadeType.ALL, fetch = FetchType.LAZY, orphanRemoval = true)
    @JoinColumn(name = "workflow_id")
    private Workflow workflow;

    @Setter
    @NotNull
    @Column(nullable = false)
    private String name;

    @Setter
    private String description;

    @NotNull
    @Column(nullable = false, updatable = false)
    @CreatedDate
    private LocalDateTime createdAt;

    @LastModifiedDate
    private LocalDateTime updatedAt;

    @Setter
    @NotNull
    @Column(nullable = false)
    private boolean isActive;

    @PrePersist
    public void prePersist() { // 최초 생성시 실행
        if (this.appId == null) {
            this.appId = generateUUID();
        }
        this.isActive = true;
    }
}
