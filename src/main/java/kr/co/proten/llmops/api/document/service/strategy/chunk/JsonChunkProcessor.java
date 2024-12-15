package kr.co.proten.llmops.api.document.service.strategy.chunk;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.io.*;
import java.util.List;

@Component
public class JsonChunkProcessor implements ChunkProcessor {
    private final Logger log = LoggerFactory.getLogger(this.getClass());
    public static final String CHUNK_TYPE = "json";

    @Override
    public String getServiceType() { return CHUNK_TYPE; }

    @Override
    public String readFileContent(String filePath) throws IOException {
        return "";
    }

    @Override
    public List<String> createChunks(String fileContent, int chunkSize, int overlap) {
        return List.of();
    }
}