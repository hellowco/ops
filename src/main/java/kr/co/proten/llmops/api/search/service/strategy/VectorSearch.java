package kr.co.proten.llmops.api.search.service.strategy;

import kr.co.proten.llmops.api.document.dto.DocumentDTO;
import kr.co.proten.llmops.api.document.entity.Document;
import kr.co.proten.llmops.api.document.service.factory.EmbeddingProcessorFactory;
import kr.co.proten.llmops.api.document.service.strategy.embedding.EmbeddingProcessor;
import kr.co.proten.llmops.api.search.repository.SearchRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

@Component
public class VectorSearch implements VectorSearchProcessor {
    private final Logger log = LoggerFactory.getLogger(this.getClass());
    public static final String SEARCH_TYPE = "vector";

    private final SearchRepository searchRepository;
    private final EmbeddingProcessorFactory embeddingProcessorFactory;

    public VectorSearch(SearchRepository searchRepository, EmbeddingProcessorFactory embeddingProcessorFactory) {
        this.searchRepository = searchRepository;
        this.embeddingProcessorFactory = embeddingProcessorFactory;
    }

    @Override
    public String getServiceType() { return SEARCH_TYPE; }

    @Override
    public List<DocumentDTO> search(String indexName, String knowledgeName, String modelType, String query, int k, int page, int pageSize) {
        EmbeddingProcessor embeddingProcessor = embeddingProcessorFactory.getEmbeddingService(modelType)
                .orElseThrow(() -> new UnsupportedOperationException("지원하지 않는 임베딩 형식: " + modelType));

        List<Double> doubleList = embeddingProcessor.embed(query);
        log.info("query embedded as {}", doubleList.get(0));
        AtomicInteger index = new AtomicInteger(0);

        float[] vectorQuery = doubleList.stream()
                .map(Double::floatValue)
                .collect(() -> new float[doubleList.size()],
                        (array, value) -> array[index.getAndIncrement()] = value,
                        (array1, array2) -> {});

        List<Document> documentList = searchRepository.vectorSearch(indexName, knowledgeName, vectorQuery, k, page, pageSize);

        return documentList.stream()
                .map(DocumentDTO::fromEntity)
                .toList();
    }
}