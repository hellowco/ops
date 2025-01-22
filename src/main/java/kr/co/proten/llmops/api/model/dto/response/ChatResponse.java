package kr.co.proten.llmops.api.model.dto.response;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class ChatResponse {
    private String content;
    private String finishReason;

    public ChatResponse(String content, String finishReason) {
        // 모델에서 생성한 데이터
        this.content = content;
        // 생성 종료 이유
        this.finishReason = finishReason;
    }
}