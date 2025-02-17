package kr.co.proten.llmops.api.user.dto.request;

import lombok.Data;

@Data
public class UserUpdateDTO {
    private String username;
    private String password; // 비밀번호 수정시 사용 (옵션)
}
