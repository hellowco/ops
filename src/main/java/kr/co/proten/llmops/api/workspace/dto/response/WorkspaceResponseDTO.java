package kr.co.proten.llmops.api.workspace.dto.response;

import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class WorkspaceResponseDTO {
    public String workspaceId;
    public String name;
    public String description;
    public LocalDateTime createdAt;
    public LocalDateTime updatedAt;
    public int tokenLimit;
    public boolean isActive;
}
