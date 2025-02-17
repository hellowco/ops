package kr.co.proten.llmops.api.user.dto.response;

import lombok.*;

import java.time.LocalDateTime;

@Builder
@ToString
@Data
public class UserDTO {
    private String userId;
    private String username;
    private String email;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
