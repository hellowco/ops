package kr.co.proten.llmops.api.document.service.factory;

import kr.co.proten.llmops.api.document.service.strategy.preprocess.ChunkProcessorPlugin;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.function.Function;

@Component
public class ChunkProcessorRegistry {
    private final List<ChunkProcessorPlugin> plugins;

    @Autowired
    ChunkProcessorRegistry(List<ChunkProcessorPlugin> plugins) {
        this.plugins = plugins;
    }

    public List<Function<String, String>> getProcessors(List<String> requestedKeys) {
        return plugins.stream()
                .filter(plugin -> requestedKeys.stream().anyMatch(plugin::supports)) // 요청 키 중 하나라도 지원하는 플러그인 필터링
                .map(ChunkProcessorPlugin::getProcessor)
                .toList();
    }
}
