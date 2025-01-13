package kr.co.proten.llmops.api.workflow.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;
import java.util.List;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class FlowNode {
    private String id; // 노드 ID
    private NodeData data; // 노드 데이터

    @Data
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class NodeData {
        private String desc; // 설명
        // start는 1개고, end는 여러개 가능
        private String type; // 노드 타입 (e.g., start, knowledge-retrieval) <- 분기
        private String title; // 노드 제목
        private boolean selected; // 선택 여부 true -> 패널창
//        private List<Variable> variables; // 노드의 변수 리스트

//        @Data
//        public static class Variable {
//            private String type; // 변수 타입 (e.g., text-input, select)
//            private String label; // 변수 라벨
//            private List<String> options; // 옵션 리스트
//            private boolean required; // 필수 여부
//            private String variable; // 변수 이름
//            private int maxLength; // 최대 길이
//        }
    }
}
