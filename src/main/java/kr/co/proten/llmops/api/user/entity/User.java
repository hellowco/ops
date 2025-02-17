package kr.co.proten.llmops.api.user.entity;

import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static kr.co.proten.llmops.core.helpers.UUIDGenerator.generateUUID;

@Builder
@Getter
@Setter
@Entity
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "users", indexes = {
        @Index(name = "idx_user_name", columnList = "username")
})
@EntityListeners(AuditingEntityListener.class)
public class User {
    @Id
    @Column(unique = true, name = "user_id", nullable = false, updatable = false)
    private String userId;

    @Column(nullable = false)
    private String username;

    @Column(unique = true, nullable = false)
    private String email;

    @Column(nullable = false)
    private String password;

    @Column(nullable = false)
    private String role;

    // Auditing 필드: 생성일과 수정일은 자동 업데이트됨
    @CreatedDate
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @LastModifiedDate
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    // 한 User는 여러 UserWorkspace와 연관 (워크스페이스별 소속 정보)
    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private List<UserWorkspace> userWorkspaces = new ArrayList<>();

    /**
     * SSO 환경에서 신규 사용자를 생성할 때 사용하는 생성자.
     * - username을 기반으로 email 자동 생성
     * - 기본 비밀번호는 username + "1!" 을 passwordEncoder로 해시 처리
     */
    public User(String userId, String username, PasswordEncoder passwordEncoder) {
        this.userId = userId;
        this.username = username;
        this.email = username + "@proten.co.kr";
        this.password = passwordEncoder.encode(username + "1!");
        this.role = "USER";
    }
}
