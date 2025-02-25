package kr.co.proten.llmops.api.model.service.impl;

import kr.co.proten.llmops.api.model.dto.request.ModelListRequest;
import kr.co.proten.llmops.api.model.dto.request.ModelRequest;
import kr.co.proten.llmops.api.model.entity.Provider;
import kr.co.proten.llmops.api.model.repository.ProviderRepository;
import kr.co.proten.llmops.core.config.ai.OllamaConfig;
import kr.co.proten.llmops.core.exception.InvalidInputException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.ai.ollama.api.OllamaApi;
import org.springframework.stereotype.Service;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class OllamaModelServiceImpl extends AbstractModelService {

    private final OllamaConfig ollamaConfig;
    private final ProviderRepository providerRepository;

    @Override
    public String getProviderType() {
        return "OLLAMA";
    }

    @Override
    protected ChatModel createChatModel(ModelRequest request) {
        String baseUrl = getProvider().getBaseURL();
        OllamaApi api = createOllamaApi(baseUrl);

        return ollamaConfig.createChatModel(api, request.model());
    }

    @Override
    public List<String> getEmbedModelList(ModelListRequest request) {
        OllamaApi api = createOllamaApi(resolveBaseUrl(request));

        try {
            List<String> embedModelList = ollamaConfig.getModelList(api);
            log.info("Ollama embedding model List: {}", embedModelList);
            return embedModelList;
        } catch (Exception e) {
            throw new InvalidInputException("Could not get embed model list from config");
        }
    }

    @Override
    public List<String> getSearchModelList(ModelListRequest request) {
        OllamaApi api = createOllamaApi(resolveBaseUrl(request));

        try {
            List<String> searchModelList = ollamaConfig.getModelList(api);
            log.info("Ollama search model List: {}", searchModelList);
            return searchModelList;
        } catch (Exception e) {
            throw new InvalidInputException("Could not get search model list from config");
        }
    }

    @Override
    public int getEmbeddingDimensions(String name) {
        String baseUrl = getProvider().getBaseURL();
        OllamaApi api = createOllamaApi(baseUrl);

        try {
            int dimension = ollamaConfig.getEmbedDimension(api, name);
            log.info("Ollama dimension: {}", dimension);
            return dimension;
        } catch (Exception e) {
            throw new InvalidInputException("Could not get embedding dimensions from config");
        }
    }

    private String resolveBaseUrl(ModelListRequest request) {
        Provider provider = getProvider();
        return (request.baseURL() != null) ? request.baseURL() : provider.getBaseURL();
    }

    private Provider getProvider() {
        return providerRepository.findByName(getProviderType())
                .orElseThrow(() -> new InvalidInputException("Provider not found"));
    }

    private OllamaApi createOllamaApi(String baseUrl) {
        try {
            URL url = new URL(baseUrl);
            String hostWithProtocol = url.getProtocol() + "://" + url.getHost();
            int port = (url.getPort() == -1) ? url.getDefaultPort() : url.getPort();
            return ollamaConfig.createClient(hostWithProtocol, port);
        } catch (MalformedURLException e) {
            throw new InvalidInputException("Invalid Base URL for Ollama configuration.");
        }
    }
}
