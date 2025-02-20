package kr.co.proten.llmops.api.workspace.dto.response;

import lombok.Builder;
import lombok.Data;
import lombok.ToString;

@Builder
@ToString
@Data
public class UserRoleDTO {
    private String userId;
    private String username;
    private String email;
    private String role;
}
