package kr.co.proten.llmops.api.user.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class SignupDTO {

    @Schema(description = "사용자 ID", example = "test1")
    private String userId;

    @Schema(description = "사용자 이름", example = "test user")
    private String username;

    @Schema(description = "사용자 비밀번호", example = "password1!")
    private String password;

    @Schema(description = "사용자 권한", example = "user")
    private String role;
}
