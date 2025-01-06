package kr.co.proten.llmops.api.app.repository;

import kr.co.proten.llmops.api.app.entity.AppEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AppRepository extends JpaRepository<AppEntity, String> {
}
