package kr.co.proten.llmops.api.model.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

import static kr.co.proten.llmops.core.helpers.UUIDGenerator.generateUUID;

@ToString
@Entity
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(
        name = "models",
        uniqueConstraints = @UniqueConstraint(columnNames = {"name", "provider_id", "type_id"})
)
@EntityListeners(AuditingEntityListener.class)
public class Model {

    @Id
    @Column(name = "model_id")
    private String modelId;

    @NotNull
    @Column(nullable = false, updatable = false)
    private String name;

    @Column(name = "index_name")
    private String indexName;

    // Type과의 관계: 모델은 하나의 타입만 갖습니다.
    @NotNull
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "type_id", nullable = false)
    private ModelType type;

    // Provider와의 관계: 모델은 하나의 프로바이더만 갖습니다.
    @NotNull
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "provider_id", nullable = false)
    private Provider provider;

    // Auditing 필드: 생성일과 수정일은 자동 업데이트됨
    @CreatedDate
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @LastModifiedDate
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @PrePersist
    public void prePersist() { // 최초 저장시 실행
        if (this.modelId == null) {
            this.modelId = generateUUID();
        }
    }
}
