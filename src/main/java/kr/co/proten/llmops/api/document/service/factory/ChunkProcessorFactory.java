package kr.co.proten.llmops.api.document.service.factory;

import kr.co.proten.llmops.api.document.service.strategy.chunk.ChunkProcessor;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Component
public class ChunkProcessorFactory {
    private final Map<String, ChunkProcessor> chunkServiceMap = new HashMap<String, ChunkProcessor>();

    public ChunkProcessorFactory(List<ChunkProcessor> chunkProcessors) {
        chunkProcessors.forEach(s -> chunkServiceMap.put(s.getServiceType(), s));
    }

    public Optional<ChunkProcessor> getChunkService(String chunkType) {
        return Optional.ofNullable(chunkServiceMap.get(chunkType));
    }
}