package kr.co.proten.llmops.api.user.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;

public record UserLoginDTO (

        @Schema(description = "사용자 ID", example = "test1")
        String userId,

        @Schema(description = "사용자 비밀번호", example = "password1!")
        String password
){}
