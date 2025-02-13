package kr.co.proten.llmops.api.user.dto.request;

public record UserLoginDTO (
    String userId,
    String password
){}
