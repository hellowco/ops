package kr.co.proten.llmops.api.workflow.entity;

import groovy.util.logging.Slf4j;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import org.hibernate.annotations.DynamicUpdate;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.time.LocalDateTime;
import java.util.Map;

import static kr.co.proten.llmops.core.helpers.DateUtil.generateCurrentTimestamp;
import static kr.co.proten.llmops.core.helpers.UUIDGenerator.generateUUID;

@lombok.extern.slf4j.Slf4j
@Slf4j
@Entity
@Builder
@AllArgsConstructor
@NoArgsConstructor
@DynamicUpdate
@Getter
@Table(name = "workflows")
public class Workflow {

    @Id
    @Column(name = "workflow_id")
    private String workflowId;

    @Setter
    @NotNull
    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "graph", columnDefinition = "jsonb", nullable = false)
    private Map<String, Object> graph;

    @NotNull
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;

    @PrePersist
    public void prePersist() {
        if (this.workflowId == null) {
            this.workflowId = generateUUID(); // ID 생성
        }
        this.createdAt = generateCurrentTimestamp(); // 최초 저장 시 생성 시간 설정
    }

    @PreUpdate
    public void preUpdate() {
        this.updatedAt = generateCurrentTimestamp(); // 수정 시마다 업데이트 시간 설정
    }
}

