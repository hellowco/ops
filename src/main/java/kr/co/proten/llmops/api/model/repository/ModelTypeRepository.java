package kr.co.proten.llmops.api.model.repository;

import kr.co.proten.llmops.api.model.entity.ModelType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ModelTypeRepository extends JpaRepository<ModelType, Long> {

    Optional<ModelType> findByType(String type);
}
