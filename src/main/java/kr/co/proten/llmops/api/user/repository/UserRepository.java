package kr.co.proten.llmops.api.user.repository;

import kr.co.proten.llmops.api.user.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, String> {
    Optional<User> findByUserId(String userId);

    Page<User> findByUsernameContainingIgnoreCase(String keyword, Pageable pageable);
}
