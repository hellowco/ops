package kr.co.proten.llmops.api.model.dto.response;

import lombok.Builder;
import org.springframework.ai.chat.metadata.Usage;

@Builder
public record ChatResponse(String content, String finishReason, Usage usage) {
}
