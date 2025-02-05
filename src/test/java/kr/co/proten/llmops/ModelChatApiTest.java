package kr.co.proten.llmops;

import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;
import reactor.test.StepVerifier;

public class ModelChatApiTest {

    private final WebClient webClient = WebClient.create("http://127.0.0.1:8080");

    @Test
    public void testModelChatEndpoint() {
        // Given
        ModelRequest request = new ModelRequest("llama3.1:8b", "한국어로 답해줘.", "오늘 서울의 날씨는?");

        // When
        Flux<ChatResponse> responseFlux = webClient.post()
                .uri("/api/model/stream")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(request)
                .retrieve()
                .bodyToFlux(ChatResponse.class);

        // Then
        StepVerifier.create(responseFlux)
                .expectNextMatches(response -> {
                    // Verify the content of each response
                    System.out.println("Response: " + response);
                    return response != null && !response.getResult().isEmpty();
                })
                .expectComplete() // Expect the stream to complete without errors
                .verify();
    }

    // Test Data Model for ModelRequest
    public static class ModelRequest {
        private final String model;
        private final String instruction;
        private final String query;

        public ModelRequest(String model, String instruction, String query) {
            this.model = model;
            this.instruction = instruction;
            this.query = query;
        }

        public String getModel() {
            return model;
        }

        public String getInstruction() {
            return instruction;
        }

        public String getQuery() {
            return query;
        }
    }

    // Test Data Model for ChatResponse
    public static class ChatResponse {
        private String result;
        private String status;

        public String getResult() {
            return result;
        }

        public String getStatus() {
            return status;
        }

        public void setResult(String result) {
            this.result = result;
        }

        public void setStatus(String status) {
            this.status = status;
        }
    }
}
