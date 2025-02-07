package kr.co.proten.llmops.api.node.dto;

import lombok.Getter;
import lombok.ToString;

import java.util.Map;

@Getter
@ToString
public class NodeResponse {
    private final String status;
    private final String nodeType;
    private final String id; // nodeId or workflowId
    private final Map<String, Object> message;

    public NodeResponse(String status, String id, String nodeType) {
        this(status, nodeType, id, null);
    }

    public NodeResponse(String status, String nodeType, String id, Map<String, Object> message) {
        this.status = status;
        this.nodeType = nodeType;
        this.id = id;
        this.message = message;
    }
}