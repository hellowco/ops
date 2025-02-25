package kr.co.proten.llmops.api.model.service.impl;

import kr.co.proten.llmops.api.knowledge.service.KnowledgeService;
import kr.co.proten.llmops.api.model.dto.request.ModelListRequest;
import kr.co.proten.llmops.api.model.dto.request.ModelRequest;
import kr.co.proten.llmops.api.model.dto.request.ModelUserRequest;
import kr.co.proten.llmops.api.model.dto.response.ChatResponse;
import kr.co.proten.llmops.api.model.dto.response.ModelResponseDTO;
import kr.co.proten.llmops.api.model.dto.response.ProviderResponseDTO;
import kr.co.proten.llmops.api.model.entity.Model;
import kr.co.proten.llmops.api.model.entity.ModelType;
import kr.co.proten.llmops.api.model.entity.Provider;
import kr.co.proten.llmops.api.model.repository.ModelRepository;
import kr.co.proten.llmops.api.model.repository.ModelTypeRepository;
import kr.co.proten.llmops.api.model.repository.ProviderRepository;
import kr.co.proten.llmops.api.model.service.ModelService;
import kr.co.proten.llmops.api.model.service.ProviderService;
import kr.co.proten.llmops.api.model.service.factory.ProviderFactory;
import kr.co.proten.llmops.core.data.ProviderData;
import kr.co.proten.llmops.core.exception.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Flux;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
public class ProviderServiceImpl implements ProviderService {

    private final ProviderFactory providerFactory;
    private final ModelRepository modelRepository;
    private final KnowledgeService knowledgeService;

    private static final String SUCCESS = "success";
    private static final String SEARCH = "SEARCH";
    private static final String EMBED = "EMBED";
    private final ProviderRepository providerRepository;
    private final ModelTypeRepository modelTypeRepository;

    @Override
    public Flux<ChatResponse> streamChat(ModelRequest modelRequest) {
        // vsearch, openai, ollama
        ModelService modelService = providerFactory.getProvider(modelRequest.provider().toUpperCase())
                .orElseThrow(() -> new UnsupportedModelException("Provider is not supported."));

        return modelService.processChat(modelRequest);
    }

    @Override
    @Transactional
    public Map<String, Object> getModelList(ModelListRequest request) {
        Map<String, Object> result = new HashMap<>();

        if(request.provider().equalsIgnoreCase("ollama")){
            validateOllama(request);
        }

        if(request.provider().equalsIgnoreCase("openai")){
            validateOpenAPI(request);
        }

        ModelService modelService = providerFactory.getProvider(request.provider().toUpperCase())
                .orElseThrow(() -> new UnsupportedModelException("Provider is not supported."));

        List<String> modelList = new ArrayList<>();
        if(request.type().equalsIgnoreCase(EMBED)){
            modelList = modelService.getEmbedModelList(request);
        }else if(request.type().equalsIgnoreCase(SEARCH)){
            modelList = modelService.getSearchModelList(request);
        }

        result.put("status", SUCCESS);
        result.put("message", "모델 리스트 반환 성공");
        result.put("response", modelList);

        return result;
    }

    private void validateOpenAPI(ModelListRequest request) {
        // OPENAI provider 엔티티 조회 (없으면 예외 발생)
        Provider openAIEntity = providerRepository.findByName("OPENAI")
                .orElseThrow(() -> new ResourceNotFoundException("OPENAI provider not found"));

        if(request.apiKey() != null && !request.apiKey().isBlank()){
            openAIEntity.setApiKey(request.apiKey());
            providerRepository.save(openAIEntity);
            log.info("Updated OpenAI API Key: {}", openAIEntity.getApiKey());
        } else {
            if(openAIEntity.getApiKey() == null || openAIEntity.getApiKey().isBlank()){
                throw new InvalidInputException("API Key must exist in order to use Open AI.");
            }
            log.info("Using existing OpenAI API Key: {}", openAIEntity.getApiKey());
        }
    }

    private void validateOllama(ModelListRequest request) {
        Provider ollamaEntity = providerRepository.findByName("OLLAMA")
                .orElseThrow(() -> new ResourceNotFoundException("OLLAMA provider not found"));

        if(request.baseURL() != null && !request.baseURL().isBlank()){
            ollamaEntity.setBaseURL(request.baseURL());
            providerRepository.save(ollamaEntity);
            log.info("Updated Ollama Base URL: {}", ollamaEntity.getBaseURL());
        } else {
            if(ollamaEntity.getBaseURL() == null || ollamaEntity.getBaseURL().isBlank()){
                throw new InvalidInputException("Base URL must exist in order to use Ollama.");
            }
            log.info("Using existing Ollama Base URL: {}", ollamaEntity.getBaseURL());
        }
    }

    @Override
    @Transactional(readOnly = true)
    public Map<String, Object> getAllModelList(String provider, String modelType) {
        Map<String, Object> result = new HashMap<>();

        Provider providerEntity = providerRepository.findByName(provider.toUpperCase())
                .orElseThrow(() ->new ResourceNotFoundException("Provider doesn't exist."));

        ModelType modelTypeEntity = modelTypeRepository.findByType(modelType.toUpperCase())
                .orElseThrow(() ->new ResourceNotFoundException("ModelType doesn't exist."));


        List<ModelResponseDTO> modelList =
                modelRepository.findModelByProviderAndType(providerEntity,modelTypeEntity)
                        .stream()
                        .map(ModelResponseDTO::entityToResponseDTO)
                        .toList();

        log.debug("model list from db: {}", modelList);

        result.put("status", SUCCESS);
        result.put("message", "모델 리스트 반환 성공");
        result.put("response", modelList);

        return result;
    }

    @Transactional(readOnly = true)
    public Map<String, Object> getModelList(String modelType) {
        Map<String, Object> result = new HashMap<>();

        // modelType에 해당하는 ModelType 엔티티 조회
        ModelType modelTypeEntity = modelTypeRepository.findByType(modelType.toUpperCase())
                .orElseThrow(() -> new ResourceNotFoundException("ModelType doesn't exist."));

        // 각 provider별 모델 리스트를 저장할 맵
        Map<String, List<ModelResponseDTO>> allProviderModels = new HashMap<>();

        // providerFactory에 등록된 모든 ModelService 를 순회
        for (ModelService service : providerFactory.getAllServices()) {
            String providerKey = service.getProviderType();
            Provider providerEntity = providerRepository.findByName(providerKey.toUpperCase())
                    .orElseThrow(() -> new ResourceNotFoundException("Provider doesn't exist: " + providerKey));

            List<ModelResponseDTO> modelList = modelRepository.findModelByProviderAndType(providerEntity, modelTypeEntity)
                    .stream()
                    .map(ModelResponseDTO::entityToResponseDTO)
                    .toList();

            allProviderModels.put(providerKey, modelList);
        }

        result.put("status", SUCCESS);
        result.put("message", "모델 리스트 반환 성공");
        result.put("response", allProviderModels);

        return result;
    }

    @Override
    @Transactional
    public Map<String, Object> saveModel(ModelUserRequest modelUserRequest) {
        Map<String, Object> result = new HashMap<>();

        ModelService modelService = providerFactory.getProvider(modelUserRequest.provider().toUpperCase())
                .orElseThrow(() -> new UnsupportedModelException("Provider is not supported."));
        log.info("Selected model provider: {}", modelService.getProviderType());

        // 임베딩 모델 차원 구하기
        int dimension = modelService.getEmbeddingDimensions(modelUserRequest.name());

        Provider provider = providerRepository.findByName(modelUserRequest.provider().toUpperCase())
                .orElseThrow(() ->new ResourceNotFoundException("Provider doesn't exist."));

        ModelType modelType = modelTypeRepository.findByType(modelUserRequest.modelType().toUpperCase())
                .orElseThrow(() ->new ResourceNotFoundException("ModelType doesn't exist."));

        // 인덱스 생성
        String indexModelName = null;
        if(modelUserRequest.modelType().equalsIgnoreCase("embed")){
            indexModelName = knowledgeService.createIndex(modelUserRequest.name(), dimension);
        }

        Model model = Model.builder()
                .name(modelUserRequest.name())
                .provider(provider)
                .type(modelType)
                .indexName(indexModelName)
                .build();

        // DB 저장
        Model savedModel = modelRepository.save(model);

        result.put("status", SUCCESS);
        result.put("message", "모델 저장 성공");
        result.put("response", ModelResponseDTO.entityToResponseDTO(savedModel));

        return result;
    }

    @Override
    @Transactional
    public Map<String, Object> deleteModel(String modelId) {
        Map<String, Object> result = new HashMap<>();

        Model model = modelRepository.findById(modelId)
                .orElseThrow(() -> new ResourceNotFoundException("Model doesn't exist."));

        if(model.getType().getType().equalsIgnoreCase("embed")) {
            try {
                knowledgeService.deleteIndex(model.getIndexName());
            } catch (Exception e) {
                throw new IndexDeleteException("Error while deleting index:{} during model deletion.");
            }
        }

        modelRepository.deleteById(modelId);

        result.put("status", SUCCESS);
        result.put("message", "모델 삭제 성공");
        result.put("response", null);

        return result;
    }

    @Override
    public Map<String, Object> getProviderList() {
        Map<String, Object> result = new HashMap<>();

        List<Provider> providerList = providerRepository.findAll();
        List<ProviderResponseDTO> responseDTOList = providerList
                .stream()
                .map(ProviderResponseDTO::entityToResponse)
                .toList();

        result.put("status", SUCCESS);
        result.put("message", "모델 제공자 리스트 반환 성공!");
        result.put("response", responseDTOList);

        return result;
    }
}
