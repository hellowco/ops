package kr.co.proten.llmops.api.auth.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@Entity
public class RefreshToken {

    @Id
    private String userId;

    private String token;

    public RefreshToken(String userId, String token) {
        this.userId = userId;
        this.token = token;
    }

    public void updateToken(String token) {
        this.token = token;
    }
}
