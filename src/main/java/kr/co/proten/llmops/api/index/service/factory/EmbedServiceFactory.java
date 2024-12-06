package kr.co.proten.llmops.api.index.service.factory;

import kr.co.proten.llmops.api.index.service.EmbeddingProcessor;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Component
public class EmbedServiceFactory {
    private final Map<String, EmbeddingProcessor> chunkServiceMap = new HashMap<>();

    public EmbedServiceFactory(List<EmbeddingProcessor> EmbeddingProcessor) {
        EmbeddingProcessor.forEach(s -> chunkServiceMap.put(s.getServiceType(), s));
    }

    public Optional<EmbeddingProcessor> getEmbeddingService(String embedType) {
        return Optional.ofNullable(chunkServiceMap.get(embedType));
    }
}