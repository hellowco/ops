package kr.co.proten.llmops.api.user.dto.request;

import lombok.Builder;
import lombok.Data;

@Builder
@Data
public class SignupDTO {
    private String userId;
    private String username;
    private String password; // 입력된 경우 기본 생성 비밀번호(username + "1!") 대신 사용
    private String role;     // 입력된 경우, 없으면 기본 "USER"
}
