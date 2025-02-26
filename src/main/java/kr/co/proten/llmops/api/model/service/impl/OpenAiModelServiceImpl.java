package kr.co.proten.llmops.api.model.service.impl;

import kr.co.proten.llmops.api.model.dto.request.ModelListRequest;
import kr.co.proten.llmops.api.model.dto.request.ModelRequest;
import kr.co.proten.llmops.api.model.entity.Provider;
import kr.co.proten.llmops.api.model.repository.ProviderRepository;
import kr.co.proten.llmops.core.config.ai.OpenAiConfig;
import kr.co.proten.llmops.core.exception.InvalidInputException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.ai.openai.api.OpenAiApi;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class OpenAiModelServiceImpl extends AbstractModelService {

    private final OpenAiConfig openAiConfig;
    private final ProviderRepository providerRepository;

    @Value("${spring.ai.openai.api-key}")
    private String apiKey;

    @Override
    public String getProviderType() {
        return "OPENAI";
    }

    @Override
    protected ChatModel createChatModel(ModelRequest request) {
        apiKey = request.apiKey() == null ? apiKey : request.apiKey();
        return openAiConfig.createChatModel(apiKey, request.model());
    }

    @Override
    public List<String> getEmbedModelList(ModelListRequest modelListRequest) {
        /*
            text-embedding-3-large : 3072
            text-embedding-3-small : 1536
            // 위의 두 모델은 dimensions 옵션으로 차원 변경 가능

            text-embedding-ada-002 : 1536
        */
        List<String> embedModelList = Arrays.stream(OpenAiApi.EmbeddingModel.values())
                .map(OpenAiApi.EmbeddingModel::getValue)
                .toList();

        log.info("openAI embedding model List: {}", embedModelList);

        return embedModelList;
    }

    @Override
    public List<String> getSearchModelList(ModelListRequest modelListRequest) {
        List<String> searchModelList = Arrays.stream(OpenAiApi.ChatModel.values())
                .map(OpenAiApi.ChatModel::getValue)
                .toList();

        log.info("openAI search model List: {}", searchModelList);

        return searchModelList;
    }

    @Override
    public int getEmbeddingDimensions(String name) {
        List<Double> embedding = getEmbedding(name, "hi");

        int dimension = embedding.size();

        log.info("Ollama embedding dimension: {}", dimension);

        return dimension;
    }

    @Override
    public List<Double> getEmbedding(String modelName, String text) {
        Provider provider = providerRepository.findByName(getProviderType())
                .orElseThrow(() -> new InvalidInputException("Provider not found"));

        apiKey = provider.getApiKey() == null ? apiKey : provider.getApiKey();

        try {
            float[] floatEmbeddings = openAiConfig.getEmbed(openAiConfig.createClient(apiKey), modelName, "hi");

            if (floatEmbeddings == null || floatEmbeddings.length == 0) {
                throw new InvalidInputException("Embedding response is empty for model: " + modelName);
            }

            List<Double> embedding = new ArrayList<>(floatEmbeddings.length);
            for (float value : floatEmbeddings) {
                embedding.add((double) value);
            }

            return embedding;
        } catch (Exception e) {
            throw new InvalidInputException("Could not get embedding from " + modelName);
        }
    }
}
