package kr.co.proten.llmops.api.workflow.dto;

import java.time.LocalDateTime;
import java.util.Map;

public class WorkflowDto {

    private String workflowId;
    private String name;
    private Map<String, Object> graph;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private boolean isActive;

}
