package kr.co.proten.llmops.api.app.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;

@Entity
@Table(name = "apps")
public class AppsEntity {

    @Id
    private String appId;

    @ManyToOne(cascade = CascadeType.REMOVE, fetch = FetchType.EAGER)
    private String workspaceId;

    @NotNull
    private String name;

    private String description;

    private String createdAt;

    private String updatedAt;

    private boolean isActive;
}
