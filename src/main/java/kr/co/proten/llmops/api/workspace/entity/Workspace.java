package kr.co.proten.llmops.api.workspace.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import kr.co.proten.llmops.api.app.entity.AppEntity;
import kr.co.proten.llmops.api.user.entity.UserWorkspace;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static kr.co.proten.llmops.core.helpers.UUIDGenerator.generateUUID;

@ToString
@Entity
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "workspaces")
@EntityListeners(AuditingEntityListener.class)
public class Workspace {

    @Id
    @Column(name = "workspace_id")
    private String workspaceId;

    @NotNull
    @Column(unique = true, nullable = false, updatable = false)
    private String name;

    @Setter
    private String description;

    // Auditing 필드: 생성일과 수정일은 자동 업데이트됨
    @CreatedDate
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @LastModifiedDate
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @Setter
    @NotNull
    @Column(nullable = false)
    private boolean isActive;

    @Setter
    @NotNull
    @Column(nullable = false)
    private int tokenLimit;

    @OneToMany(mappedBy = "workspace", cascade = CascadeType.REMOVE, orphanRemoval = true, fetch = FetchType.LAZY)
    @JsonIgnore
    private List<AppEntity> apps = new ArrayList<>();

    // 중계 엔티티와 1:N 관계 (워크스페이스 하나에 여러 사용자 연결 정보)
    @OneToMany(mappedBy = "workspace", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    @JsonIgnore
    private List<UserWorkspace> userWorkspaces = new ArrayList<>();

    @PrePersist
    public void prePersist() { // 최초 저장시 실행
        if (this.workspaceId == null) {
            this.workspaceId = generateUUID();
        }
        this.isActive = true;
    }
}
