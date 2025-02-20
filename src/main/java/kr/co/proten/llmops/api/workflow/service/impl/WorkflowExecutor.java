package kr.co.proten.llmops.api.workflow.service.impl;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import kr.co.proten.llmops.api.node.dto.Node;
import kr.co.proten.llmops.api.node.dto.NodeResponse;
import kr.co.proten.llmops.api.node.service.NodeExecutionService;
import kr.co.proten.llmops.api.workflow.dto.FlowNode;
import kr.co.proten.llmops.api.workflow.helper.ExecutionContext;
import kr.co.proten.llmops.core.exception.NodeNotFoundException;
import kr.co.proten.llmops.core.exception.WorkflowExecutionException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.*;
import java.util.function.Function;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

@Slf4j
@Component
public class WorkflowExecutor {
    private final NodeExecutionService nodeExecutionService;
    private final ObjectMapper objectMapper;
    private final Pattern PLACEHOLDER_PATTERN;

    public WorkflowExecutor(NodeExecutionService nodeExecutionService, ObjectMapper objectMapper, @Value("${workflow.placeholder.pattern}") String regexPattern) {
        this.nodeExecutionService = nodeExecutionService;
        this.objectMapper = objectMapper;
        this.PLACEHOLDER_PATTERN = Pattern.compile(regexPattern);
    }

    public Flux<NodeResponse> executeDAG(List<FlowNode> nodes, Map<String, List<String>> dag, String workflowId, String query) {
        Map<String, Node> nodeMap = nodes.stream()
                .map(this::mapFlowNodeToNode)
                .collect(Collectors.toMap(Node::getId, Function.identity()));
        ExecutionContext context = new ExecutionContext();
        TopologicalSorter sorter = new TopologicalSorter(dag);
        Queue<String> executionQueue = new LinkedList<>(sorter.getSortedNodes());

        Flux<NodeResponse> workflowStart = Flux.just(
                new NodeResponse("WORKFLOW_STARTED", "workflow", workflowId, null)
        );

        Flux<NodeResponse> nodeExecutions = Flux.fromIterable(executionQueue)
                .concatMap(nodeId -> {
                    log.info("Executing nodeId {}", nodeId);
                    Node node = nodeMap.get(nodeId);
                    if (node == null) {
                        return Flux.error(new NodeNotFoundException("Node not found: " + nodeId));
                    }

                    Map<String, Object> processedInput = processInputRecursively(
                            node.getInput(), context, nodeMap
                    );
                    node.setInput(processedInput);
                    node.setQuery(query);

                    return nodeExecutionService.executeNode(node, context)
                            .onErrorMap(e -> {
                                log.error(e.getMessage());
                                return new WorkflowExecutionException("Node execution failed: " + nodeId);
                            })
                            .doOnComplete(() -> log.info("Ending nodeId {}", nodeId));
                })
                .doOnComplete(() -> log.info("From nodeExecution Ending workflow {}", workflowId))
                .cache();

        Mono<NodeResponse> workflowFinish = nodeExecutions
                .filter(lastResponse -> lastResponse.getMessage() != null)
                .last()  // Flux의 마지막 요소(NodeResponse)를 추출
                .map(lastResponse -> {
                    log.info("lastResponse: {}", lastResponse.toString());
                    return new NodeResponse("WORKFLOW_FINISHED", "workflow", workflowId, lastResponse.getMessage());
                });

        return workflowStart
                .concatWith(nodeExecutions)
                .concatWith(workflowFinish)
                .doOnError(ex -> log.error("Error in workflow DAG: {}", ex.getMessage(), ex))
                .onErrorResume(ex -> {
                    NodeResponse errorResponse =
                            new NodeResponse("WORKFLOW_ERROR", "workflow", workflowId, Map.of("error", ex.getMessage()));
                    return Flux.just(errorResponse); // or multiple, if you want
                });
    }

    // 재귀적 입력 처리
    private Map<String, Object> processInputRecursively(
            Map<String, Object> input,
            ExecutionContext context,
            Map<String, Node> nodeMap
    ) {
        Map<String, Object> processed = new LinkedHashMap<>();
        input.forEach((key, value) -> {
            Object newValue = processValue(value, context, nodeMap);
//            log.info("key:{}, newValue:{}", key, newValue);

            processed.put(key, newValue);

        });
        return processed;
    }

    private Object processValue(Object value, ExecutionContext context, Map<String, Node> nodeMap) {
        if (value instanceof String strValue) {
            // 문자열 전체가 플레이스홀더인 경우
            if (isFullPlaceholder(strValue)) {
                // 플레이스홀더 부분만 추출하여 resolveValue 호출
                Matcher matcher = PLACEHOLDER_PATTERN.matcher(strValue);
                if (matcher.matches()) {
                    String sourceNodeId = matcher.group(1);  // 예: "1735867390736"
                    String fieldPath = matcher.group(2);     // 예: "results"
                    Object resolved = resolveValue(sourceNodeId, fieldPath, context, nodeMap);
                    // 치환 실패가 아니라면 resolved 객체를 그대로 반환
                    if (resolved != null && !"MISSING_FIELD".equals(resolved)) {
                        return resolved;
                    }
                    // 실패 시에는 원래 플레이스홀더 문자열 반환
                }
            }
            // 플레이스홀더가 포함되어 있더라도 전체가 아닌 경우에는 기존 방식대로 문자열 치환
            return replacePlaceholders(strValue, context, nodeMap);
        } else if (value instanceof Map) {
            return processMap((Map<?, ?>) value, context, nodeMap);
        } else if (value instanceof List) {
            return processList((List<?>) value, context, nodeMap);
        }
        return value;
    }

    private boolean isFullPlaceholder(String template) {
        return PLACEHOLDER_PATTERN.matcher(template).matches();
    }

    private Map<String, Object> processMap(Map<?, ?> map, ExecutionContext context, Map<String, Node> nodeMap) {
        Map<String, Object> processed = new LinkedHashMap<>();
        map.forEach((k, v) -> {
            if (k instanceof String) {
                processed.put((String) k, processValue(v, context, nodeMap));
            }
        });
        return processed;
    }

    private List<Object> processList(List<?> list, ExecutionContext context, Map<String, Node> nodeMap) {
        return list.stream()
                .map(item -> processValue(item, context, nodeMap))
                .collect(Collectors.toList());
    }

    private String replacePlaceholders(String template, ExecutionContext context, Map<String, Node> nodeMap) {
        Matcher matcher = PLACEHOLDER_PATTERN.matcher(template);
        StringBuffer sb = new StringBuffer();

        while (matcher.find()) {
            // 예: {{#1735867390736.output#}} 전체 매치
            String wholePlaceholder = matcher.group(0);  // "{{#...#}}"
            String sourceNodeId = matcher.group(1);      // "1735867390736"
            String fieldPath = matcher.group(2);         // "output"

            Object resolvedValue = resolveValue(sourceNodeId, fieldPath, context, nodeMap);

            if (resolvedValue == null || "MISSING_FIELD".equals(resolvedValue)) {
                // 치환 실패 시 -> 원본 placeholder 유지
                matcher.appendReplacement(sb, Matcher.quoteReplacement(wholePlaceholder));
            } else {
                // 정상 치환
                matcher.appendReplacement(sb, Matcher.quoteReplacement(String.valueOf(resolvedValue)));
            }
        }
        matcher.appendTail(sb);

        return sb.toString();
    }

    private Object resolveValue(String sourceNodeId, String fieldPath,
                                ExecutionContext context, Map<String, Node> nodeMap) {
        try {
            // 컨텍스트에서 실행 결과 먼저 조회
            if (context.hasOutput(sourceNodeId)) {
                return resolveFieldPath(context.getOutput(sourceNodeId), fieldPath);
            }

            // 실행 전 노드의 경우 기본 입력값 사용
            Node sourceNode = nodeMap.get(sourceNodeId);
            if (sourceNode != null) {
                return resolveFieldPath(sourceNode.getInput(), fieldPath);
            }

            return "UNDEFINED";
        } catch (Exception e) {
            return "ERROR: " + e.getMessage();
        }
    }

    private Object resolveFieldPath(Map<String, Object> data, String fieldPath) {
        String[] parts = fieldPath.split("\\.");
        Object current = data;
        for (String part : parts) {
            if (!(current instanceof Map)) return null;
            current = ((Map<?, ?>) current).get(part);
            if (current == null) break;
        }
        return current != null ? current : "MISSING_FIELD";
    }

    // 기존 유틸리티 메서드 유지
    private Node mapFlowNodeToNode(FlowNode flowNode) {
        log.debug("mapFlowNodeToNode:{}", flowNode);
        Node node = new Node(flowNode.getId(), flowNode.getData().getType());
        node.setInput(convertNodeDataToMap(flowNode.getData()));
        return node;
    }

    private Map<String, Object> convertNodeDataToMap(FlowNode.NodeData nodeData) {
        // 1) nodeData 전체를 먼저 Map으로 변환합니다.
        Map<String, Object> resultMap = objectMapper.convertValue(nodeData, new TypeReference<Map<String, Object>>() {
        });

        // 2) nodeData 안에 datasets 필드가 있고 비어있지 않다면
        if (nodeData.getDatasets() != null && !nodeData.getDatasets().isEmpty()) {

            // 3) 각 Dataset 객체도 Map으로 변환합니다.
            List<Map<String, Object>> datasetMapList = nodeData.getDatasets().stream()
                    .map(dataset -> {
                        // 각 Dataset 객체 -> Map
                        Map<String, Object> dsMap = objectMapper.convertValue(dataset, new TypeReference<Map<String, Object>>() {
                        });

                        // 필요 시, 추가 가공 예시 (예: type 필드 주입)
                        dsMap.put("type", nodeData.getType());
                        return dsMap;
                    })
                    .collect(Collectors.toList());

            // 4) 변환한 datasetMapList를 최종 resultMap에 덮어씌웁니다.
            resultMap.put("datasets", datasetMapList);
        }

        // 필요 시 로깅
        log.info("convertNodeDataToMap result: {}", resultMap);

        return resultMap;
    }

    // 위상 정렬 내부 클래스
    private static class TopologicalSorter {
        private final Map<String, Integer> inDegree = new HashMap<>();
        private final Map<String, List<String>> graph;

        public TopologicalSorter(Map<String, List<String>> dag) {
            this.graph = new HashMap<>(dag);
            initializeInDegrees();
        }

        private void initializeInDegrees() {
            graph.values().forEach(neighbors ->
                    neighbors.forEach(node ->
                            inDegree.put(node, inDegree.getOrDefault(node, 0) + 1)
                    )
            );
            graph.keySet().forEach(node ->
                    inDegree.putIfAbsent(node, 0)
            );
        }

        public Queue<String> getSortedNodes() {
            log.info("getSortedNodes called");

            Queue<String> queue = new LinkedList<>();
            inDegree.entrySet().stream()
                    .filter(entry -> entry.getValue() == 0)
                    .forEach(entry -> queue.add(entry.getKey()));

            Queue<String> result = new LinkedList<>();
            while (!queue.isEmpty()) {
                String node = queue.poll();
                result.add(node);
                graph.getOrDefault(node, Collections.emptyList())
                        .forEach(neighbor -> {
                            inDegree.put(neighbor, inDegree.get(neighbor) - 1);
                            if (inDegree.get(neighbor) == 0) {
                                queue.add(neighbor);
                            }
                        });
            }

            if (result.size() != inDegree.size()) {
                throw new WorkflowExecutionException("Cycle detected in DAG, unable to perform topological sort.");
            }

            log.info("getSortedNodes returns {}", result);
            return result;
        }
    }
}