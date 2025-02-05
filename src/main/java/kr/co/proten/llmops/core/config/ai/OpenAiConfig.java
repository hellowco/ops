package kr.co.proten.llmops.core.config.ai;

import io.micrometer.observation.ObservationRegistry;
import org.springframework.ai.ollama.management.ModelManagementOptions;
import org.springframework.ai.openai.OpenAiChatOptions;
import org.springframework.ai.openai.api.OpenAiApi;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.ai.openai.OpenAiChatModel;
import org.springframework.util.Assert;

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
        OpenAiApi api = createClient(apiKey);
        OpenAiChatOptions defaultOptions = new OpenAiChatOptions.Builder()
                .model(model)
                .temperature(0.2)
                .maxTokens(4096)
                .streamUsage(true)
                .build();

        return new OpenAiChatModel(api, defaultOptions);
    }
}
