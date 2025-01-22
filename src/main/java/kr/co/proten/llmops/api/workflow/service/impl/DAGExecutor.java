package kr.co.proten.llmops.api.workflow.service.impl;

import kr.co.proten.llmops.api.node.dto.Node;
import kr.co.proten.llmops.api.node.dto.NodeResponse;
import kr.co.proten.llmops.api.workflow.helper.DAG;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;

import java.util.*;

@Slf4j
@Component
public class DAGExecutor {

    public Flux<NodeResponse> executeDAG(DAG dag) {
        Map<String, List<String>> graph = dag.getGraph();
        Map<String, Node> nodes = dag.getNodes(); // 노드 정보 매핑 (노드 ID -> 노드 객체)
        Queue<Node> queue = new LinkedList<>();
        Map<String, Integer> inDegree = calculateInDegrees(graph);

        // 초기 큐 설정: in-degree가 0인 노드를 추가
        for (String nodeId : inDegree.keySet()) {
            if (inDegree.get(nodeId) == 0) {
                queue.add(nodes.get(nodeId));
            }
        }

        // Flux를 통해 노드 실행
        return Flux.create(sink -> {
            while (!queue.isEmpty()) {
                Node currentNode = queue.poll();
                log.info("Starting execution of node: {}", currentNode.getId());

                // 실행 시작 이벤트 전송
                sink.next(new NodeResponse("START", currentNode.getId()));

                // 노드 실행
                try {
                    Object output = currentNode.executeNode();
                    currentNode.setOutput(output);

                    // 실행 완료 이벤트 전송
                    sink.next(new NodeResponse("END", currentNode.getId(), output));

                    log.info("Finished execution of node: {}", currentNode.getId());
                } catch (Exception e) {
                    sink.error(new RuntimeException("Error executing node: " + currentNode.getId(), e));
                    return;
                }

                // 다음 노드의 in-degree 감소 및 큐에 추가
                for (String neighborId : graph.getOrDefault(currentNode.getId(), new ArrayList<>())) {
                    inDegree.put(neighborId, inDegree.get(neighborId) - 1);
                    if (inDegree.get(neighborId) == 0) {
                        queue.add(nodes.get(neighborId));
                    }
                }
            }

            // 모든 노드가 실행되었는지 검증
            if (inDegree.values().stream().anyMatch(degree -> degree > 0)) {
                sink.error(new IllegalStateException("Not all nodes were executed. DAG might be incomplete"));
            } else {
                sink.complete();
            }
        });
    }

    private Map<String, Integer> calculateInDegrees(Map<String, List<String>> graph) {
        Map<String, Integer> inDegree = new HashMap<>();
        for (String node : graph.keySet()) {
            inDegree.put(node, 0); // 초기화
        }
        for (List<String> neighbors : graph.values()) {
            for (String neighbor : neighbors) {
                inDegree.put(neighbor, inDegree.getOrDefault(neighbor, 0) + 1);
            }
        }
        return inDegree;
    }
}
