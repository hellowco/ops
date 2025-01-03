/*
package kr.co.proten.llmops.api.workspace.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import kr.co.proten.llmops.api.app.entity.AppsEntity;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "workspaces")
public class WorkspacesEntity {

    @Id
    @GeneratedValue
    private String workspaceId;

    private String name;
    private String description;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    private boolean isActive;

    @Column(name = "token_limit")
    private int tokenLimit;

    @OneToMany(mappedBy = "workspace", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<AppsEntity> apps = new ArrayList<>();

    // Getters and Setters
}
*/
