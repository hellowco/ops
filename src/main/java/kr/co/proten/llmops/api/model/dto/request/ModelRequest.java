package kr.co.proten.llmops.api.model.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;

import java.util.List;

public record ModelRequest(

        @NotBlank
        @Schema(description = "모델 제공자(ollama/openai)", example = "ollama")
        String provider,

        @NotBlank
        @Schema(description = "모델명(EEVE-Ko-Instruct-10.8B-v1.0-Q8_0:latest/gpt-4o", example = "llama3.1:8b")
        String model,

        @Schema(description = "openAI용 apiKey", example = "sk-proj-로 시작하는 본인키")
        String apiKey,

        @NotBlank
        @Schema(description = "시스템 프롬프트", example = "- context XML Node 안에 내용을 기반으로 질문에 대해 정확한 답을 찾고 없으면 \"적절한 답변을 찾을 수 없습니다.\" 이라고 답을 하시오. \n - 답은 한국어로 하며, 창작을 하지 않는다. \n - Question 의 내용을 답변에 인용하지 않고, 동일한 답변을 반복하지 않는다. \n - 꼭, 결론이나 요약된 내용을 답을 하시오.")
        String instruction,

        @NotBlank
        @Schema(description = "유저 프롬프트", example = "문서의 핵심 내용을 알려줘.")
        String query,

        @NotBlank
        @Schema(description = "지식 결과 문서 리스트", example = "[\"urher refne the model for the agent’s tasks by providing it with examples that showcase the agent’s capabilities, including instances of the agent using specifc tools or reasoning steps in various con\"," +
                                                        "\"ting system and the agent. (i.e. 1 incoming event/ query and 1 agent response) No native tool implementation. Tools are natively implemented in agent architecture. No native logic layer implemented. U\"]")
        List<String> documents
) { }
