package kr.co.proten.llmops.api.app.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.annotation.Nullable;

import java.time.LocalDateTime;

import static kr.co.proten.llmops.core.helpers.DateUtil.generateCurrentTimestamp;

public record AppUpdateDTO(
        @Schema(description = "앱 UUID", example = "ab88840c-9b36-4ed5-abfb-dc616544f14b-1736137815")
        String app_id,

        @Schema(description = "앱 이름", example = "ProRAG")
        String name,

        @Schema(description = "앱 설명", example = "기본적인 Naive RAG")
        String description,

        @Schema(description = "앱 마지막 수정일시(자동생성)", example = "2025-01-06 15:30:45")
        @Nullable
        LocalDateTime updated_at
) {
        public static AppUpdateDTO createDefault(String app_id, String name, String description) {
                return new AppUpdateDTO(app_id, name, description, generateCurrentTimestamp());
        }
}
