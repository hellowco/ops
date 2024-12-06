package kr.co.proten.llmops.api.index.dto;

import lombok.Getter;

import java.util.List;

@Getter
public final class ProcessingResult {
    private final List<String> chunks;
    private final List<List<Double>> embeddings;

    private ProcessingResult(List<String> chunks, List<List<Double>> embeddings) {
        this.chunks = List.copyOf(chunks);
        this.embeddings = (embeddings == null) ? null : List.copyOf(embeddings);
    }

    public static ProcessingResult of(List<String> chunks, List<List<Double>> embeddings) {
        return new ProcessingResult(chunks, embeddings);
    }
}
