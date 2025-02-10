package kr.co.proten.llmops.api.workflow.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@ToString
@Data
@NoArgsConstructor
@AllArgsConstructor
public class WorkflowResponseDTO {
        private String workflowId;
        private String workflowData;
        private String createdAt;
        private String updatedAt;
}
