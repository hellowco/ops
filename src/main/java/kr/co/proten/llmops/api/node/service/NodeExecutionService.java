package kr.co.proten.llmops.api.node.service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import kr.co.proten.llmops.api.document.dto.DocumentDTO;
import kr.co.proten.llmops.api.model.dto.request.ModelRequest;
import kr.co.proten.llmops.api.model.service.ModelService;
import kr.co.proten.llmops.api.model.service.factory.ProviderFactory;
import kr.co.proten.llmops.api.node.dto.Node;
import kr.co.proten.llmops.api.node.dto.NodeResponse;
import kr.co.proten.llmops.api.search.dto.SearchRequestDTO;
import kr.co.proten.llmops.api.search.service.SearchService;
import kr.co.proten.llmops.api.workflow.dto.FlowNode;
import kr.co.proten.llmops.api.workflow.helper.ExecutionContext;
import kr.co.proten.llmops.core.exception.NodeExecutionException;
import kr.co.proten.llmops.core.exception.UnsupportedModelException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@RequiredArgsConstructor
@Service
public class NodeExecutionService {

    private final ProviderFactory providerFactory;
    private final SearchService searchService;
    private final ObjectMapper mapper;

    public Flux<NodeResponse> executeNode(Node node, ExecutionContext context) {
        return Flux.defer(() -> {
//            Map<String, Object> processedInput = mergeInputWithContext(node.getInput(), context);

            Flux<NodeResponse> startFlux = Flux.just(
                    new NodeResponse("NODE_STARTED", node.getType(), node.getId())
            );

            Flux<Map<String, Object>> executionFlux;
            switch (node.getType().toLowerCase()) {
                case "llm":
                    Flux<Map<String, Object>> llmChunks = executeLLM(node).cache();
                    Mono<Map<String, Object>> aggregatedResult = llmChunks
                            .collectList()
                            .map(this::aggregateLLMOutput);
                    executionFlux = llmChunks.concatWith(aggregatedResult.flux());
                    break;
                case "start":
                    executionFlux = Flux.just(executeStart(node));
                    break;
                case "knowledge-retrieval":
                    executionFlux = Flux.just(executeKnowledgeRetrieval(node));
                    break;
                case "end":
                    executionFlux = Flux.just(executeEnd(node));
                    break;
                default:
                    return Flux.error(new UnsupportedOperationException("Unsupported node type: " + node.getType()));
            }

            return startFlux.concatWith(
                    executionFlux
                            .map(output -> {
                                node.setOutput(output);
                                context.saveOutput(node.getId(), output);
                                return new NodeResponse("NODE_PROGRESS", node.getType(), node.getId(), output);
                            })
                            .concatWithValues(new NodeResponse("NODE_FINISHED", node.getType(), node.getId(), node.getOutput()))
                            .onErrorMap(e -> {
                                log.error(e.getMessage());
                                return new NodeExecutionException("Node execution failed: " + node.getId());
                            })
            );
        });
    }

    private Map<String, Object> aggregateLLMOutput(List<Map<String, Object>> chunks) {
        // Example aggregation: concatenate all 'text' fields
        String combinedText = chunks.stream()
                .map(chunk -> (String) chunk.get("text"))
                .collect(Collectors.joining());
        Map<String, Object> aggregated = new HashMap<>();
        aggregated.put("text", combinedText);
        // Include other fields as necessary, or handle multiple fields
        return aggregated;
    }

    private Map<String, Object> executeStart(Node node) {
        log.debug("Executing Start Node: {}", node.getId());

        Map<String, Object> output = new HashMap<>();
        output.put("query", node.getQuery()); // 사용자 입력 쿼리 전달
        output.put("timestamp", System.currentTimeMillis());

        log.debug("Start Node output: {}", output);
        return output;
    }

    private Map<String, Object> executeKnowledgeRetrieval(Node node) {
        log.debug("Executing Knowledge Retrieval Node: {}", node);

        // 데이터셋 설정 파싱
        List<Map<String, String>> datasets = (List<Map<String, String>>) node.getInput().get("datasets");
        log.debug("Executing Knowledge Retrieval Datasets: {}", datasets);

        List<List<DocumentDTO>> results = new ArrayList<>();
        // 지식 검색 실행
        for (Map<String, String> dataset : datasets) {
            SearchRequestDTO searchRequest = SearchRequestDTO.builder()
                    .modelName(dataset.get("model_name"))
                    .knowledgeName(dataset.get("knowledge_name"))
                    .modelType(dataset.get("model_type"))
                    .query(dataset.get("query")) // Start 노드에서 전달된 쿼리 사용
                    .searchType(dataset.get("search_type"))
                    .keywordWeight(dataset.get("keyword_weight"))
                    .vectorWeight(dataset.get("vector_weight"))
                    .k(dataset.get("k"))
                    .page(dataset.get("page"))
                    .pageSize(dataset.get("page_size"))
                    .build();

            log.debug("Knowledge Retrieval searchRequest: {}", searchRequest);
            Map<String, Object> searchResult = searchService.search(searchRequest);
            log.debug("Knowledge Retrieval searchResult: {}", searchResult);
            List<DocumentDTO> response = (List<DocumentDTO>) searchResult.get("response");
            results.add(response);
        }

        Map<String, Object> output = new HashMap<>();
        output.put("results", results);
        output.put("count", results.size());

        return output;
    }

    private Flux<Map<String, Object>> executeLLM(Node node) {
        log.debug("Executing LLM Node: {}", node);

        Map<String, Object> llm_settings = mapper.convertValue(
                node.getInput().get("llm_settings"),
                new TypeReference<Map<String, Object>>() {}
        );
        log.debug("LLM settings: {}", llm_settings);

//        printValueTypes(llm_settings, "");

        List<List<List<DocumentDTO>>> contextList = mapper.convertValue(
                llm_settings.get("context"),
                new TypeReference<>() {}
        );

        List<FlowNode.NodeData.Prompt> prompt = mapper.convertValue(
                llm_settings.get("prompt_template"),
                new TypeReference<>() {}
        );
        log.debug("Prompt: {}", prompt);

        FlowNode.NodeData.LLMModel llmModel = mapper.convertValue(llm_settings.get("model"), FlowNode.NodeData.LLMModel.class);

        // 지식 검색에서 나온 결과 하나의 배열로 전환
        List<String> stringResults = contextList.stream()
                .flatMap(List::stream)
                .flatMap(List::stream)
                .map(DocumentDTO::content)
                .toList();
        log.debug("Executing LLM stringResults: {}", stringResults);

        String systemPrompt = "";
        String userPrompt = "";

        // prompt 에 context 있을 시, 지식검색에서 나온 값으로 대체
        for (FlowNode.NodeData.Prompt p : prompt) {
            if(p.getText().contains("{{#context#}}")) {
                p.setText(p.getText().replace("{{#context#}}", stringResults.toString()));
                stringResults = new ArrayList<>();
            }
            if(p.getRole().equals("system")){
                systemPrompt = p.getText();
            }else if(p.getRole().equals("user")){
               userPrompt = p.getText();
            }
        }

        ModelRequest modelRequest = ModelRequest.builder()
                .provider(llmModel.getProvider())
                .model(llmModel.getName())
                .apiKey(llmModel.getApiKey())
                .instruction(systemPrompt)
                .query(userPrompt)
                .documents(stringResults)
                .build();

        ModelService modelService = providerFactory.getProvider(modelRequest.provider().toUpperCase())
                .orElseThrow(() -> new UnsupportedModelException("Provider is not supported."));

        return modelService.processChat(modelRequest)
                .map(chatResponse -> {
                    Map<String, Object> output = new HashMap<>();
                    output.put("text", chatResponse.content());
                    return output;
                });
    }

    private Map<String, Object> executeEnd(Node node) {
//        log.info("Executing End Node: {}", node);

        List<String> variables = (List<String>) node.getInput().get("variables");
        StringBuilder result = new StringBuilder();

        for (String res : variables) {
            res = res.replaceAll("(?s)<think>.*?</think>", ""); // deepseek think 제거
            res = res.replace("✅ Stream completed successfully.", ""); // 정상적으로 끝난 신호 제거
            res = res.trim();
            result.append(res);
        }

        // 최종 결과 포맷팅
        Map<String, Object> output = new HashMap<>();
        output.put("final_result", result.toString());
        output.put("status", "COMPLETED");
        output.put("timestamp", System.currentTimeMillis());

//        log.info("Executing End Node: {}", output);
        return output;
    }
}
