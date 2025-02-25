package kr.co.proten.llmops.core.config.ai;

import kr.co.proten.llmops.core.exception.InvalidInputException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.openai.OpenAiChatModel;
import org.springframework.ai.openai.OpenAiChatOptions;
import org.springframework.ai.openai.api.OpenAiApi;
import org.springframework.ai.openai.api.OpenAiApi.Embedding;
import org.springframework.ai.openai.api.OpenAiApi.EmbeddingRequest;
import org.springframework.ai.openai.api.OpenAiApi.EmbeddingList;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.util.Assert;

@Slf4j
@Configuration
public class OpenAiConfig {

    @Value("${spring.ai.openai.api-key}")
    private String apiKey;
    private static final String BASE_URL = "https://api.openai.com";

    @Bean
    public OpenAiApi defaultOpenAiApi() {
        return new OpenAiApi(BASE_URL, apiKey);
    }

    @Bean
    public OpenAiChatModel defaultOpenAiChatModel(OpenAiApi openAiApi) {
        OpenAiChatOptions defaultOptions = new OpenAiChatOptions.Builder()
                .model("gpt-4o")
                .temperature(0.2)
                .maxTokens(4096)
                .build();

        return new OpenAiChatModel(openAiApi, defaultOptions);
    }

    /**
     * 동적 OpenAiApi 클라이언트를 생성하는 메서드
     */
    public OpenAiApi createClient(String apiKey) {
        Assert.hasText(apiKey, "API key must not be null or empty");

        try {
            return new OpenAiApi(BASE_URL, apiKey);
        } catch (Exception e) {
            throw new IllegalArgumentException("Failed to create OpenAiApi client with API Key: " + apiKey, e);
        }
    }

    /**
     * 동적 OpenAiChatModel을 생성하는 메서드
     */
    public OpenAiChatModel createChatModel(String apiKey, String model) {
        OpenAiChatOptions defaultOptions = new OpenAiChatOptions.Builder()
                .model(model)
                .temperature(0.2)
                .maxTokens(4096)
                .streamUsage(true)
                .build();
        OpenAiApi api = createClient(apiKey);

        return new OpenAiChatModel(api, defaultOptions);
    }

    public int getEmbedDimension(OpenAiApi api, String model) {
        // 임베딩 요청을 생성합니다.
        EmbeddingRequest<String> embeddingRequest = new EmbeddingRequest<>("hi", model);

        // 응답에서 임베딩 리스트를 추출합니다.
        EmbeddingList<Embedding> embeddingList = api.embeddings(embeddingRequest).getBody();
        log.info("openai embed res body: {}", embeddingList);

        if (embeddingList != null && !embeddingList.data().isEmpty()) {
            // 첫 번째 임베딩을 가져옵니다.
            Embedding embedding = embeddingList.data().get(0);

            return embedding.embedding().length;
        } else {
            throw new InvalidInputException("No embedding found");
        }
    }
}
