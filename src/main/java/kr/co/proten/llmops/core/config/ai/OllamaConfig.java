package kr.co.proten.llmops.core.config.ai;

import org.springframework.ai.ollama.OllamaChatModel;
import org.springframework.ai.ollama.api.OllamaApi;
import org.springframework.ai.ollama.api.OllamaOptions;
import org.springframework.ai.ollama.management.ModelManagementOptions;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.util.Assert;
import io.micrometer.observation.ObservationRegistry;

@Configuration
public class OllamaConfig {

    @Bean(name = "ollamaObservationRegistry")
    public ObservationRegistry ollamaObservationRegistry() {
        return ObservationRegistry.NOOP;
    }

    @Bean
    public OllamaApi defaultOllamaApi() {
        return new OllamaApi("http://localhost:11434");
    }

    @Bean
    public OllamaChatModel defaultOllamaChatModel(OllamaApi ollamaApi) {
        return OllamaChatModel.builder()
                .ollamaApi(ollamaApi)
                .defaultOptions(
                        OllamaOptions.builder()
                        .model("llama3.1:8b")
                        .build()
                )
                .observationRegistry(ollamaObservationRegistry())
                .modelManagementOptions(ModelManagementOptions.defaults())
                .build();
    }

    /**
     * 동적 OllamaClient를 생성하는 메서드
     */
    public OllamaApi createClient(String host, int port) {
        Assert.hasText(host, "host must not be null or empty");
        Assert.isTrue(port > 0 && port <= 65535, "port must be between 1 and 65535");

        try {
            String url = String.format("http://%s:%d", host, port);
            return new OllamaApi(url);
        } catch (Exception e) {
            throw new IllegalArgumentException("Failed to create OllamaApi client. Host: " + host + ", Port: " + port, e);
        }
    }

    /**
     * 동적 OllamaChatModel을 생성하는 메서드
     */
    public OllamaChatModel createChatModel(String host, int port, String model) {
        OllamaApi api = createClient(host, port);
        return OllamaChatModel.builder()
                .ollamaApi(api)
                .defaultOptions(
                        OllamaOptions.builder()
                        .model(model)
                        .build()
                )
                .observationRegistry(ollamaObservationRegistry())
                .modelManagementOptions(ModelManagementOptions.defaults())
                .build();
    }
}