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

import java.util.List;

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

    public float[] getEmbed(OpenAiApi api, String model, String text) {
        if (api == null) {
            throw new IllegalArgumentException("OpenAiApi must not be null");
        }
        if (model == null || model.isEmpty()) {
            throw new IllegalArgumentException("Model must not be null or empty");
        }
        if (text == null) {
            throw new IllegalArgumentException("Text must not be null");
        }

        EmbeddingRequest<String> embeddingRequest = new EmbeddingRequest<>(text, model);

        EmbeddingList<Embedding> embeddingList = api.embeddings(embeddingRequest).getBody();

        if (embeddingList == null) {
            throw new InvalidInputException("EmbeddingList is null for model: " + model);
        }
        List<Embedding> data = embeddingList.data();
        if (data == null || data.isEmpty()) {
            throw new InvalidInputException("No embedding found for model: " + model);
        }

        Embedding embedding = data.get(0);
        float[] embeddingArray = embedding.embedding();
        if (embeddingArray == null || embeddingArray.length == 0) {
            throw new InvalidInputException("Embedding array is empty for model: " + model);
        }

        return embeddingArray;
    }

}
