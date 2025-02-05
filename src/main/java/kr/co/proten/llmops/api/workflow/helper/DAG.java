package kr.co.proten.llmops.api.workflow.helper;

import java.util.*;

public class DAG {
    private final Map<String, List<String>> adjacencyList = new HashMap<>();

    public void addNode(String nodeId) {
        adjacencyList.putIfAbsent(nodeId, new ArrayList<>());
    }

    public void addEdge(String fromNode, String toNode) {
        if (!adjacencyList.containsKey(fromNode) || !adjacencyList.containsKey(toNode)) {
            throw new IllegalArgumentException("Both nodes must exist in the graph");
        }
        adjacencyList.get(fromNode).add(toNode);
    }

    public Map<String, List<String>> getGraph() {
        return adjacencyList;
    }
}
