package kr.co.proten.llmops.api.user.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PasswordUpdateDTO {

    @Schema(description = "현재 비밀번호", example = "password1!")
    private String password;

    @Schema(description = "새로운 비밀번호", example = "password2@")
    private String newPassword;
}
