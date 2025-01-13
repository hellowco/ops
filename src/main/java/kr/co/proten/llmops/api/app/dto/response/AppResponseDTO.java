package kr.co.proten.llmops.api.app.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@ToString
@Data
@NoArgsConstructor
@AllArgsConstructor
public class AppResponseDTO {
    private String appId;
    private String name;
    private String description;
    private String workflowId;
    private String createdAt;
    private String updatedAt;
    private boolean isActive;
}
