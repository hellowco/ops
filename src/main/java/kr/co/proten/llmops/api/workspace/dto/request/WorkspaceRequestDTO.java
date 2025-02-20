package kr.co.proten.llmops.api.workspace.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import kr.co.proten.llmops.core.validation.ValidNumber;

import java.util.List;

public record WorkspaceRequestDTO(

        @NotBlank
        @Schema(description = "워크스페이스 이름", example = "다락방")
        String name,

        @NotBlank
        @Schema(description = "설명", example = "프로텐 e-sports 동아리")
        String description,

        @NotBlank
        @ValidNumber(type = ValidNumber.NumberType.INT, message = "tokenLimit must be a valid integer")
        @Schema(description = "토큰 제한량", example = "10000")
        String tokenLimit,

        @NotNull(message = "최소 한 명 이상의 사용자가 필요합니다.")
        @Size(min = 1, message = "최소 한 명 이상의 사용자가 필요합니다.")
        @Schema(description = "사용자 목록")
        List<UserInfo> users
) {
    public record UserInfo(
            @NotBlank
            @Schema(description = "사용자 ID", example = "user123")
            String userId,

            @NotBlank
            @Schema(description = "사용자 이름", example = "홍길동")
            String username,

            @NotBlank
            @Email
            @Schema(description = "사용자 이메일", example = "user@example.com")
            String email,

            @NotBlank
            @Schema(description = "사용자 역할", example = "OWNER")
            String role
    ) {}
}
