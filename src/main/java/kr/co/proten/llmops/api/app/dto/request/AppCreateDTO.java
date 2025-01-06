package kr.co.proten.llmops.api.app.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.annotation.Nullable;

import java.time.LocalDateTime;

import static kr.co.proten.llmops.core.helpers.DateUtil.generateCurrentTimestamp;
import static kr.co.proten.llmops.core.helpers.UUIDGenerator.generateUUID;

public record AppCreateDTO(
        @Schema(description = "앱 UUID", example = "ab88840c-9b36-4ed5-abfb-dc616544f14b-1736137815")
        String app_id,

        @Schema(description = "앱 이름", example = "ProRAG")
        String name,

        @Schema(description = "앱 설명", example = "기본적인 Naive RAG")
        String description,

        @Schema(description = "앱 생성일시(자동생성)", example = "2025-01-05 15:30:45")
        @Nullable
        LocalDateTime created_at,

        @Schema(description = "앱 마지막 수정일시(자동생성)", example = "2025-01-06 15:30:45")
        @Nullable
        LocalDateTime updated_at,
        
        @Schema(description = "앱 활성여부", example = "true/false")
        boolean is_active
) {
        public static AppCreateDTO createDefault(String name, String description) {
                return new AppCreateDTO(generateUUID(), name, description, generateCurrentTimestamp(), null, true);
        }
}
