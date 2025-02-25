package kr.co.proten.llmops.api.model.repository;

import kr.co.proten.llmops.api.model.entity.Model;
import kr.co.proten.llmops.api.model.entity.ModelType;
import kr.co.proten.llmops.api.model.entity.Provider;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ModelRepository extends JpaRepository<Model, String> {

    List<Model> findModelByProviderAndType(Provider provider, ModelType modelType);
}
