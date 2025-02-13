package kr.co.proten.llmops.api.user.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class AuthResponseDto {
    private String userId;
    private String username;
    private String accessToken;
    private String refreshToken;
}