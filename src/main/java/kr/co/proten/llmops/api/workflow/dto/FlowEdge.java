package kr.co.proten.llmops.api.workflow.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class FlowEdge {
    private String id; // 엣지 ID
    private EdgeData data; // 엣지 데이터

    @Data
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class EdgeData {
        private String sourceType; // 출발 노드 타입
        private String targetType; // 도착 노드 타입
        private boolean isInIteration; // 반복 여부
    }
}
