package kr.co.proten.llmops.api.document.service.strategy.preprocess;

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