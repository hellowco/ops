package kr.co.proten.llmops.api.document.service.strategy.preprocess;

import java.util.function.Function;

public interface ChunkProcessorPlugin {
    boolean supports(String key); // API 매개변수 키 지원 여부
    Function<String, String> getProcessor(); // 처리 함수
}
