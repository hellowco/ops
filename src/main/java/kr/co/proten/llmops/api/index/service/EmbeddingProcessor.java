package kr.co.proten.llmops.api.index.service;

import java.util.List;

public interface EmbeddingProcessor {
    String getServiceType();

    List<Double> embed(String text); // 파일에서 청크한 텍스트
}