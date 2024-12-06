package kr.co.proten.llmops.api.index.service.impl;

import kr.co.proten.llmops.api.index.service.FileChunkProcessor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

@Component
public class JsonFileChunkProcessor implements FileChunkProcessor {
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