package kr.co.proten.llmops.api.index.service.impl;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.reflect.TypeToken;
import kr.co.proten.llmops.api.index.service.EmbeddingProcessor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;

@Component
public class ProsLLMEmbeddingProcessor implements EmbeddingProcessor {
    private final Logger log = LoggerFactory.getLogger(this.getClass());
    public static final String MODEL_TYPE = "ProsLLM";

    @Value("${embedding.model.prosllm}")
    private String embeddingModel;

    @Value("${embedding.url.prosllm}")
    private String embeddingURL;

    @Override
    public String getServiceType() {
        return MODEL_TYPE;
    }

    @Override
    public List<Double> embed(String text) {
        // Example: Return the text length as an embedding.
        return getBertVector(embeddingModel, text);
    }

    /**
     * @param model : embedding model
     * @param content : prompt to embed with a model
     *
     * @return bertVector: 임베딩한 벡터값을 List<Double>로 리턴
     */
    public List<Double> getBertVector(String model, String content) {
        List<Double> bertVector = null;

        try {
            // Create the URL and open the connection
            URL url = new URL(embeddingURL);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();

            // Set the request method and headers
            connection.setRequestMethod("POST");
            connection.setRequestProperty("Content-Type", "application/json; utf-8");
            connection.setRequestProperty("Accept", "application/json");
            connection.setDoOutput(true);

            // Create the JSON payload
            JsonObject jsonPayload = new JsonObject();
            jsonPayload.addProperty("indice", model);
            jsonPayload.addProperty("query", content);

            // Send the JSON payload
            try (OutputStream os = connection.getOutputStream()) {
                byte[] input = jsonPayload.toString().getBytes("utf-8");
                os.write(input, 0, input.length);
            }

            // Read the response
            try (BufferedReader br = new BufferedReader(new InputStreamReader(connection.getInputStream(), "utf-8"))) {
                StringBuilder response = new StringBuilder();
                String responseLine;
                while ((responseLine = br.readLine()) != null) {
                    response.append(responseLine.trim());
                }

                // Parse the JSON response
                JsonElement jsonElement = JsonParser.parseString(response.toString());
                JsonObject jsonObject = jsonElement.getAsJsonObject();
                JsonObject resultObject = jsonObject.getAsJsonObject("result");
                JsonElement embeddingElement = resultObject.get("embedding");

                // Convert the embedding to a list of doubles
                Gson gson = new Gson();
                bertVector = gson.fromJson(embeddingElement, new TypeToken<List<Double>>(){}.getType());
            }
        } catch (Exception e) {
            log.error("Vector Embedding failed! \n {}", e.getMessage());
            throw new RuntimeException(e);
        }

        return bertVector;
    }
}