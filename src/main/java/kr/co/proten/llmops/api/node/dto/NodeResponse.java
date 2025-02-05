package kr.co.proten.llmops.api.node.dto;

import lombok.Getter;

@Getter
public class NodeResponse {
    private final String status;
    private final String nodeId;
    private final Object data;

    public NodeResponse(String status, String nodeId) {
        this(status, nodeId, null);
    }

    public NodeResponse(String status, String nodeId, Object data) {
        this.status = status;
        this.nodeId = nodeId;
        this.data = data;
    }
}