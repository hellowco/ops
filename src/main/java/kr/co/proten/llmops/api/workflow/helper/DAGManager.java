package kr.co.proten.llmops.api.workflow.helper;

import kr.co.proten.llmops.api.workflow.dto.FlowEdge;
import kr.co.proten.llmops.api.workflow.dto.FlowNode;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class DAGManager {
    public DAG createDAG(List<FlowNode> nodeList, List<FlowEdge> edgeList) {
        DAG dag = new DAG();

        // 노드 추가
        for (FlowNode node : nodeList) {
            dag.addNode(node.getId());
        }

        // 엣지 추가
        for (FlowEdge edge : edgeList) {
            dag.addEdge(edge.getSource(), edge.getTarget());
        }

        // DAG 유효성 검사
        DAGValidator validator = new DAGValidator();
        if (validator.hasCycle(dag.getGraph())) {
            throw new IllegalStateException("Workflow contains a cycle, DAG cannot be created");
        }

        return dag;
    }
}