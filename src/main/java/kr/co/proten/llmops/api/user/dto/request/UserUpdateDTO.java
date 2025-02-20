package kr.co.proten.llmops.api.user.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserUpdateDTO {

    @Schema(description = "사용자 이메일", example = "test@proten.co.kr")
    @Email
    private String userEmail;

    @Schema(description = "사용자 부서", example = "R&D")
    private String department;

    @Schema(description = "사용자 직책", example = "manager")
    private String jobTitle;

}
