package kr.co.proten.llmops.api.model.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;

public record ModelListRequest(

        @NotBlank
        @Schema(description = "모델 제공자 (openai/ollama)", example = "ollama")
        String provider,

        @NotBlank
        @Schema(description = "임베딩/검색 여부 (embed/search)", example = "embed")
        String type,

        @Schema(description = "Ollama를 제외한 제공자의 api 키", example = "skProj-afsdfawefawef132fw", nullable = true)
        String apiKey,

        @Schema(description = "Ollama의 엔드포인트", example = "http://192.168.0.28:11434", nullable = true)
        String baseURL
) {
}
