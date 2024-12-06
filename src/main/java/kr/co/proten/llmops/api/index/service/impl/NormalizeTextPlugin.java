package kr.co.proten.llmops.api.index.service.impl;

import kr.co.proten.llmops.api.index.service.ChunkProcessorPlugin;
import org.springframework.stereotype.Component;

import java.util.function.Function;

@Component
public class NormalizeTextPlugin implements ChunkProcessorPlugin {
    @Override
    public boolean supports(String key) {
        return "normalizeText".equalsIgnoreCase(key);
    }

    @Override
    public Function<String, String> getProcessor() {
        return String::toLowerCase;
    }
}