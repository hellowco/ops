package kr.co.proten.llmops.api.app.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import kr.co.proten.llmops.api.workflow.entity.Workflow;
import kr.co.proten.llmops.api.workspace.entity.Workspace;
import lombok.*;

import java.time.LocalDateTime;

import static kr.co.proten.llmops.core.helpers.DateUtil.generateCurrentTimestamp;
import static kr.co.proten.llmops.core.helpers.UUIDGenerator.generateUUID;

@Entity
@Table(name = "apps")
@Builder
@ToString
@Getter
@NoArgsConstructor
@AllArgsConstructor
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
    private LocalDateTime createdAt;

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
        this.createdAt = generateCurrentTimestamp();
        this.isActive = true;
    }

    @PreUpdate
    public void preUpdate() {
        this.updatedAt = generateCurrentTimestamp(); // 수정 시마다 업데이트 시간 설정
    }
}
