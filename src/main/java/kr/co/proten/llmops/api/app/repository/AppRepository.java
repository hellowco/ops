package kr.co.proten.llmops.api.app.repository;

import kr.co.proten.llmops.api.app.entity.AppEntity;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface AppRepository extends JpaRepository<AppEntity, String> {
    List<AppEntity> findByNameContaining(String appName, Pageable pageable);
}
