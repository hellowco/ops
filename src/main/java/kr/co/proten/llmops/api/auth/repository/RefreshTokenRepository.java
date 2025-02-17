package kr.co.proten.llmops.api.auth.repository;

import kr.co.proten.llmops.api.auth.entity.RefreshToken;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RefreshTokenRepository extends JpaRepository<RefreshToken, String> {
}
