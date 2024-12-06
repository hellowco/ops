package kr.co.proten.llmops.api.index.service.factory;

import kr.co.proten.llmops.api.index.service.FileChunkProcessor;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Component
public class ChunkServiceFactory {
    private final Map<String, FileChunkProcessor> chunkServiceMap = new HashMap<String, FileChunkProcessor>();

    public ChunkServiceFactory(List<FileChunkProcessor> fileChunkProcessors) {
        fileChunkProcessors.forEach(s -> chunkServiceMap.put(s.getServiceType(), s));
    }

    public Optional<FileChunkProcessor> getFileChunkService(String chunkType) {
        return Optional.ofNullable(chunkServiceMap.get(chunkType));
    }
}