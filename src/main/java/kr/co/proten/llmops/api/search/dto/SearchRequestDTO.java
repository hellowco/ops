package kr.co.proten.llmops.api.search.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;

import java.util.Optional;

public record SearchRequestDTO(
        @NotBlank
        @Schema(description = "색인 모델", example = "llmops")
        String modelName,

        @NotBlank
        @Schema(description = "지식명", example = "test")
        String knowledgeName,

        @NotBlank
        @Schema(description = "임베딩 모델", example = "ProsLLM")
        String modelType,

        @NotBlank
        @Schema(description = "검색 질의어", example = "프로텐")
        String query,

        @NotBlank
        @Schema(description = "검색 타입(keyword,vector,hybrid)", example = "hybrid")
        String searchType,

        @Schema(description = "키워드 가중치", example = "0.5")
        Optional<Float> keywordWeight,

        @Schema(description = "벡터 가중치", example = "0.5")
        Optional<Float> vectorWeight,

        @Schema(description = "인접 K값 (knn의 k값)", example = "3")
        Optional<Integer> k
) {
    public SearchRequestDTO {
        // Validate keywordWeight and vectorWeight
        if (keywordWeight.isPresent() && vectorWeight.isPresent()) {
            double keyword = keywordWeight.get();
            double vector = vectorWeight.get();

            if (keyword < 0 || keyword > 1) {
                throw new IllegalArgumentException("keywordWeight must be between 0 and 1");
            }
            if (vector < 0 || vector > 1) {
                throw new IllegalArgumentException("vectorWeight must be between 0 and 1");
            }
            if (keyword + vector == 1) {
                throw new IllegalArgumentException("The sum of keywordWeight and vectorWeight must be 1");
            }
        } else if (keywordWeight.isPresent() || vectorWeight.isPresent()) {
            throw new IllegalArgumentException("Both keywordWeight and vectorWeight must be provided together, or neither must be provided.");
        }

        // Validate k
        if (k.isPresent() && k.get() <= 0) {
            throw new IllegalArgumentException("k value must be greater than 0");
        }
    }
}
