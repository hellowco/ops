package kr.co.proten.llmops.api.search.dto;

import java.util.Optional;

public record SearchRequestDTO(
        String modelName,
        String knowledgeName,
        String modelType,
        String query,
        String searchType,
        Optional<Float> keywordWeight,
        Optional<Float> vectorWeight,
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
