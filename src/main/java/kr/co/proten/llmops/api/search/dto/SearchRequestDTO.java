package kr.co.proten.llmops.api.search.dto;

import groovy.util.logging.Slf4j;
import io.swagger.v3.oas.annotations.Hidden;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import kr.co.proten.llmops.core.exception.InvalidInputException;
import kr.co.proten.llmops.core.validation.ValidNumber;
import lombok.Builder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.Assert;

@Slf4j
@Builder
public record SearchRequestDTO(
        @NotBlank
        @Schema(description = "색인 모델(오픈서치의 인덱스명)", example = "llmops")
        String modelName,

        @NotBlank
        @Schema(description = "지식명", example = "test")
        String knowledgeName,

        @NotBlank
        @Schema(description = "임베딩 모델(호출할 임베딩 모델 종류)", example = "ProsLLM")
        String modelType,

        @NotBlank
        @Schema(description = "검색 질의어", example = "프로텐")
        String query,

        @NotBlank
        @Schema(description = "검색 타입(keyword,vector,hybrid)", example = "hybrid")
        String searchType,

        @ValidNumber(type = ValidNumber.NumberType.DOUBLE, message = "keywordWeight must be a valid double")
        @Schema(description = "키워드 가중치", example = "0.5")
        String keywordWeight,

        @ValidNumber(type = ValidNumber.NumberType.DOUBLE, message = "vectorWeight must be a valid double")
        @Schema(description = "벡터 가중치", example = "0.5")
        String vectorWeight,

        @ValidNumber(type = ValidNumber.NumberType.INT, message = "k must be a valid integer")
        @Schema(description = "인접 K값 (knn의 k값)", example = "3")
        String k,

        @NotBlank
        @ValidNumber(type = ValidNumber.NumberType.INT, message = "page must be a valid integer")
        @Schema(description = "페이지 번호", example = "1")
        String page,

        @NotBlank
        @ValidNumber(type = ValidNumber.NumberType.INT, message = "pageSize must be a valid integer")
        @Schema(description = "페이지 당 수", example = "10")
        String pageSize
) {
    private static final Logger log = LoggerFactory.getLogger(SearchRequestDTO.class);
    private static final double EPSILON = 1e-6;

    public SearchRequestDTO {
        // Initialize page
        try {
            int pageNumber = Integer.parseInt(page);
            page = pageNumber < 1 ? "0" : String.valueOf(pageNumber - 1);
        } catch (NumberFormatException e) {
            throw new InvalidInputException("Page must be a valid integer");
        }
    }

    public void validate() {
        // Validate keywordWeight and vectorWeight
        if (keywordWeight != null && vectorWeight != null) {
            double keyword = parseDoubleOrThrow(keywordWeight, "keywordWeight must be a valid double");
            double vector = parseDoubleOrThrow(vectorWeight, "vectorWeight must be a valid double");

            log.debug("keyword value = {}", keyword);
            log.debug("vector value = {}", vector);

            Assert.isTrue(keyword >= 0 && keyword <= 1, "keywordWeight must be between 0 and 1");
            Assert.isTrue(vector >= 0 && vector <= 1, "vectorWeight must be between 0 and 1");
            Assert.isTrue(Math.abs(keyword + vector - 1) <= EPSILON, "The sum of keywordWeight and vectorWeight must be 1");
        } else {
            Assert.isTrue(keywordWeight == null && vectorWeight == null, "Both keywordWeight and vectorWeight must be provided together, or neither must be provided.");
        }

        // Validate k
        if (k != null) {
            int kValue = parseIntOrThrow(k, "k must be a valid integer");
            Assert.isTrue(kValue > 0, "k value must be greater than 0");
        }
    } //end of validate

    @Hidden
    public float getKeywordWeightAsFloat() {
        return (float) parseDoubleOrThrow(keywordWeight, "keywordWeight must be a valid double");
    }

    @Hidden
    public float getVectorWeightAsFloat() {
        return (float) parseDoubleOrThrow(vectorWeight, "vectorWeight must be a valid double");
    }

    @Hidden
    public int getKAsInt() {
        return parseIntOrThrow(k, "k must be a valid integer");
    }

    @Hidden
    public int getPageAsInt() {
        return parseIntOrThrow(page, "page must be a valid integer");
    }

    @Hidden
    public int getPageSizeAsInt() {
        return parseIntOrThrow(pageSize, "pageSize must be a valid integer");
    }

    private double parseDoubleOrThrow(String value, String errorMessage) {
        try {
            return Double.parseDouble(value);
        } catch (NumberFormatException e) {
            throw new InvalidInputException(errorMessage);
        }
    }

    private int parseIntOrThrow(String value, String errorMessage) {
        try {
            return Integer.parseInt(value);
        } catch (NumberFormatException e) {
            throw new InvalidInputException(errorMessage);
        }
    }
} // end of record
