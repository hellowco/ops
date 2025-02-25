package kr.co.proten.llmops.api.model.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import kr.co.proten.llmops.core.helpers.ApiKeyAttributeConverter;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@ToString
@Entity
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "providers")
@EntityListeners(AuditingEntityListener.class)
public class Provider {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long providerId;

    @NotNull
    @Column(unique = true, nullable = false)
    private String name;

    @NotNull
    @Column(nullable = false)
    private String icon;

    @Column
    private String description;

    @Setter
    @Convert(converter = ApiKeyAttributeConverter.class)
    @Column(name = "api_key", columnDefinition = "text")
    private String apiKey;

    @Setter
    @Column(name = "base_url")
    private String baseURL;

    // Auditing 필드: 생성일과 수정일은 자동 업데이트됨
    @CreatedDate
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @LastModifiedDate
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @OneToMany(mappedBy = "provider", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Model> models = new ArrayList<>();
}
