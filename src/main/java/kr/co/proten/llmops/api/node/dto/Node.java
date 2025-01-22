package kr.co.proten.llmops.api.node.dto;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;

@Data
@Slf4j
public class Node {
        private final String id;
        private final String type;
        private Object input;
        private Object process_data;
        private Object output;

        public Node(String id, String type) {
            this.id = id;
            this.type = type;
        }

        // 노드 실행 로직
        public Object executeNode() {
            log.info("Processing node: {} of type: {}", id, type);

            return switch (type) {
                case "start" -> executeStart();
                case "knowledge-retrieval" -> executeSearch();
                case "llm" -> executeLLM();
                case "end" -> executeEnd();
                default -> throw new IllegalArgumentException("Unsupported node type: " + type);
            };
        }

        private Object executeStart() {
            log.info("Executing TYPE_A logic with input: {}", input);
            return "Processed_TypeA"; // 예제 결과
        }

        private Object executeSearch() {
            log.info("Executing TYPE_B logic with input: {}", input);
            return "Processed_TypeB"; // 예제 결과
        }

        private Object executeLLM() {
            log.info("Executing TYPE_C logic with input: {}", input);
            return "Processed_TypeC"; // 예제 결과
        }

        private Object executeEnd() {
            log.info("Executing TYPE_D logic with input: {}", input);
            return "Processed_TypeD"; // 예제 결과
        }
    }