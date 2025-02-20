package kr.co.proten.llmops.api.workspace.dto.request;

public record UserPatchValue(
    String userId,
    String username,
    String email,
    String role
){}