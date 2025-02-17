package kr.co.proten.llmops.api.user.entity;

import jakarta.persistence.*;
import kr.co.proten.llmops.api.workspace.entity.Workspace;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

@Builder
@Getter
@Entity
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "user_workspace")
@EntityListeners(AuditingEntityListener.class)
public class UserWorkspace {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // 사용자와의 다대일 관계
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    // 워크스페이스와의 다대일 관계
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "workspace_id", nullable = false)
    private Workspace workspace;

    // 해당 워크스페이스 내에서 사용자의 역할 (OWNER, MEMBER)
    @Column(nullable = false)
    private String role;

    // 가입(소속) 시점: 자동 생성
    @CreatedDate
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    // 마지막 수정 시점: 자동 업데이트
    @LastModifiedDate
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
}
