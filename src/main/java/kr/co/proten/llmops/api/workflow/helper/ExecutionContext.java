package kr.co.proten.llmops.api.workflow.helper;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
@Component
public class ExecutionContext {
    private final ConcurrentHashMap<String, Map<String, Object>> nodeOutputs =
            new ConcurrentHashMap<>();

    // 노드 실행 결과 저장 (불변 맵으로 저장)
    public void saveOutput(String nodeId, Map<String, Object> output) {
        nodeOutputs.put(nodeId, Collections.unmodifiableMap(output));
        log.debug("Saved output for node {}: {}", nodeId, output);
    }

    // 특정 노드의 출력 전체 조회
    public Map<String, Object> getOutput(String nodeId) {
        log.debug("Retrieving output for node {}: {}", nodeId, nodeOutputs.get(nodeId));
        return nodeOutputs.getOrDefault(nodeId, Collections.emptyMap());
    }

    // 모든 출력을 노드 ID별로 그룹화해 반환
    public Map<String, Map<String, Object>> getAllOutputs() {
        return new HashMap<>(nodeOutputs); // 스냅샷 반환 (ConcurrentHashMap 스레드 안전)
    }

    // 특정 노드의 출력 존재 여부 확인
    public boolean hasOutput(String nodeId) {
        return nodeOutputs.containsKey(nodeId);
    }

    // 전체 컨텍스트 초기화 (테스트용)
    public void clear() {
        nodeOutputs.clear();
        log.debug("ExecutionContext cleared");
    }
}