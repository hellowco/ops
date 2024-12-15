package kr.co.proten.llmops.api.document.service.factory;

import kr.co.proten.llmops.api.document.service.strategy.embedding.EmbeddingProcessor;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Component
public class EmbeddingProcessorFactory {
    private final Map<String, EmbeddingProcessor> chunkServiceMap = new HashMap<>();

    public EmbeddingProcessorFactory(List<EmbeddingProcessor> EmbeddingProcessor) {
        EmbeddingProcessor.forEach(s -> chunkServiceMap.put(s.getServiceType(), s));
    }

    public Optional<EmbeddingProcessor> getEmbeddingService(String embedType) {
        return Optional.ofNullable(chunkServiceMap.get(embedType));
    }
}