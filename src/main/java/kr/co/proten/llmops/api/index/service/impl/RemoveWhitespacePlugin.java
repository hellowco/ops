package kr.co.proten.llmops.api.index.service.impl;

import kr.co.proten.llmops.api.index.service.ChunkProcessorPlugin;
import org.springframework.stereotype.Component;

import java.util.function.Function;

@Component
public class RemoveWhitespacePlugin implements ChunkProcessorPlugin {
    @Override
    public boolean supports(String key) {
        return "removeWhitespace".equalsIgnoreCase(key);
    }

    @Override
    public Function<String, String> getProcessor() {
        return chunk -> chunk.replaceAll("\\s+", " ");
    }
}