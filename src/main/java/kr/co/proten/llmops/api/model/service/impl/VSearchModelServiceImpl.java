package kr.co.proten.llmops.api.model.service.impl;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import kr.co.proten.llmops.api.model.dto.request.ModelListRequest;
import kr.co.proten.llmops.api.model.dto.request.ModelRequest;
import kr.co.proten.llmops.core.exception.FailedModelConnectionException;
import kr.co.proten.llmops.core.exception.InvalidInputException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
public class VSearchModelServiceImpl extends AbstractModelService {

    private final RestTemplate restTemplate;
    private final ObjectMapper objectMapper;

    @Value("${embedding.url.list}")
    String embeddingListUrl;

    @Value("${embedding.url.vsearch}")
    String embeddingUrl;

    @Override
    protected ChatModel createChatModel(ModelRequest request) {
        log.error("VSearch model doesn't support chatting model");
        throw new InvalidInputException("VSearch model doesn't support chatting model");
    }

    @Override
    public String getProviderType() {
        return "VSEARCH";
    }

    @Override
    public List<String> getEmbedModelList(ModelListRequest request) {
        List<String> embedModelList;
        try {
            // GET 요청 수행, 응답을 JsonNode 형태로 받음
            ResponseEntity<JsonNode> response = restTemplate.getForEntity(embeddingListUrl, JsonNode.class);

            if (response.getStatusCode() == HttpStatus.OK) {
                // 응답 본문에서 JsonNode 객체를 가져옴
                JsonNode root = response.getBody();
                // "result" -> "modelInfo" -> "loaded_data" 경로의 배열을 가져옴
                assert root != null;
                JsonNode loadedData = root.path("result").path("modelInfo").path("loaded_data");

                embedModelList = new ArrayList<>();
                // 배열인지 확인 후 반복문으로 값 추가
                if (loadedData.isArray()) {
                    for (JsonNode modelNode : loadedData) {
                        embedModelList.add(modelNode.asText());
                    }
                } else {
                    throw new InvalidInputException("Invalid data format: 'loaded_data' is not an array");
                }
            } else {
                throw new InvalidInputException("Failed to retrieve embed model list, status code: " + response.getStatusCode());
            }
        } catch (Exception e) {
            throw new InvalidInputException("Could not get embed model list from config");
        }

        log.info("VSearch embedding model List: {}", embedModelList);
        return embedModelList;
    }

    @Override
    public List<String> getSearchModelList(ModelListRequest request) {
        log.error("VSearch model doesn't support chatting model");
        throw new InvalidInputException("VSearch model doesn't support chatting model");
    }

    @Override
    public int getEmbeddingDimensions(String name) {
        log.info("name of model: {}", name);

        int dimension = getEmeddings(name, "hi").size();

        log.info("VSearch dimension: {}", dimension);

        return dimension;
    }

    @Override
    public List<Double> getEmbedding(String modelName, String plainText) {
        return getEmeddings(modelName, plainText);
    }

    private List<Double> getEmeddings (String modelName, String query) {
        Map<String, Object> requestBody = new HashMap<>();
        requestBody.put("model", modelName);
        requestBody.put("query", query); // test query

        try {
            // HTTP 요청 헤더 설정
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);

            // Map을 JSON 문자열로 변환
            String jsonRequest = objectMapper.writeValueAsString(requestBody);
            HttpEntity<String> requestEntity = new HttpEntity<>(jsonRequest, headers);

            // POST 요청 보내기
            ResponseEntity<JsonNode> response = restTemplate.postForEntity(embeddingUrl, requestEntity, JsonNode.class);

            List<Double> embeddings = new ArrayList<>();

            if (response.getStatusCode() == HttpStatus.OK) {
                // 응답 본문 파싱
                JsonNode root = response.getBody();
                // "result" -> "embedding" 경로의 배열 추출
                assert root != null;
                JsonNode embeddingArray = root.path("result").path("embedding");

                if (embeddingArray.isArray()) {
                    for (JsonNode node : embeddingArray) {
                        embeddings.add(node.asDouble());
                    }
                } else {
                    throw new InvalidInputException("Invalid response format: 'embedding' is not an array");
                }
            } else {
                throw new FailedModelConnectionException("Could not get response from {}" + embeddingUrl);
            }

            return embeddings;
        } catch (Exception e) {
            throw new InvalidInputException("Could not get embedding from {}" + modelName);
        }
    }

}