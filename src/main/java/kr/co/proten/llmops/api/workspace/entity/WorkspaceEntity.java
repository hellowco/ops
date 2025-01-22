package kr.co.proten.llmops.api.workspace.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import kr.co.proten.llmops.api.app.entity.AppEntity;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static kr.co.proten.llmops.core.helpers.DateUtil.generateCurrentTimestamp;
import static kr.co.proten.llmops.core.helpers.UUIDGenerator.generateUUID;

@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "workspaces")
public class WorkspaceEntity {

    @Id
    @Column(name = "workspace_id")
    private String workspaceId;

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

    @NotNull
    @Column(nullable = false)
    private int tokenLimit;

    @OneToMany(mappedBy = "workspace", cascade = CascadeType.REMOVE, orphanRemoval = true, fetch = FetchType.LAZY)
    private List<AppEntity> apps = new ArrayList<>();

    //@OneToMany(mappedBy = "workspace") // 중계 테이블과 1:N 관계
    //private List<UserWorkspace> userWorkspaces = new ArrayList<>();

    @PrePersist
    public void prePersist() { // 최초 저장시 실행
        if (this.workspaceId == null) {
//            this.workspaceId = generateUUID();
            this.workspaceId = "8ee589ef-c7bb-4f2a-a773-630abd0de8c7";
        }
        this.createdAt = generateCurrentTimestamp();
        this.isActive = true;
    }

    @PreUpdate
    public void preUpdate() {
        this.updatedAt = generateCurrentTimestamp(); // 수정 시마다 업데이트 시간 설정
    }
}
