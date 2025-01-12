package kr.co.proten.llmops.api.workspace.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import kr.co.proten.llmops.api.app.entity.AppEntity;
import lombok.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "workspaces")
public class WorkspaceEntity {

    @Id
    @Column(name = "workspace_id")
    private String workspaceId;

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

    @NotNull
    @Column(nullable = false)
    private int tokenLimit;

    @OneToMany(mappedBy = "workspace", cascade = CascadeType.REMOVE, orphanRemoval = true, fetch = FetchType.LAZY)
    private List<AppEntity> apps = new ArrayList<>();

    //@OneToMany(mappedBy = "workspace") // 중계 테이블과 1:N 관계
    //private List<UserWorkspace> userWorkspaces = new ArrayList<>();
}
