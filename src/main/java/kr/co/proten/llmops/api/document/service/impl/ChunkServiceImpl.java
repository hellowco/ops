package kr.co.proten.llmops.api.document.service.impl;

import kr.co.proten.llmops.api.document.dto.DocumentDTO;
import kr.co.proten.llmops.api.document.entity.Document;
import kr.co.proten.llmops.api.document.entity.Metadata;
import kr.co.proten.llmops.api.document.repository.opensearch.OpenSearchChunkRepository;
import kr.co.proten.llmops.api.document.repository.opensearch.OpenSearchDocumentRepository;
import kr.co.proten.llmops.api.document.service.ChunkService;
import kr.co.proten.llmops.api.document.service.factory.EmbeddingProcessorFactory;
import kr.co.proten.llmops.api.document.service.strategy.embedding.EmbeddingProcessor;
import kr.co.proten.llmops.api.document.util.DocumentBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

import static kr.co.proten.llmops.core.helpers.MappingLoader.convertToMap;
import static kr.co.proten.llmops.core.helpers.UUIDGenerator.generateUUID;

@Service
public class ChunkServiceImpl implements ChunkService {
    private final Logger log = LoggerFactory.getLogger(this.getClass());

    private final OpenSearchChunkRepository openSearchChunkRepository;
    private final EmbeddingProcessorFactory embeddingProcessorFactory;
    private final OpenSearchDocumentRepository openSearchDocumentRepository;

    public ChunkServiceImpl(OpenSearchChunkRepository openSearchChunkRepository, EmbeddingProcessorFactory embeddingProcessorFactory, OpenSearchDocumentRepository openSearchDocumentRepository) {
        this.openSearchChunkRepository = openSearchChunkRepository;
        this.embeddingProcessorFactory = embeddingProcessorFactory;
        this.openSearchDocumentRepository = openSearchDocumentRepository;
    }

    @Override
    public Map<String, Object> createChunk(String indexName, String knowledgeName, String docId, String content, String modelType) {
        return executeWithResult(() -> {
            EmbeddingProcessor embeddingProcessor = embeddingProcessorFactory.getEmbeddingService(modelType)
                    .orElseThrow(() -> new UnsupportedOperationException("지원하지 않는 임베딩 형식: " + modelType));

            Metadata metadata = getAndUpdateMetadata(indexName, knowledgeName, docId);
            long updatedChunkNum = metadata.getChunkNum();

            DocumentBuilder builder = new DocumentBuilder();
            Document document = builder.createDocument(
                    knowledgeName,
                    docId,
                    updatedChunkNum,
                    content,
                    embeddingProcessor.embed(content)
            );

            Document savedDocument = openSearchChunkRepository.saveChunk(indexName, document);

            return createSuccessResult("문서 청크 성공", DocumentDTO.builder().build().fromEntity(savedDocument));
        });
    }

    @Override
    public Map<String, Object> readChunk(String indexName, String knowledgeName, String docId, long chunkId) {
        return executeWithResult(() -> {
            Document document = openSearchChunkRepository.getChunkByChunkId(indexName, knowledgeName, docId, chunkId);
            return createSuccessResult("문서 청크 가져오기 성공", DocumentDTO.builder().build().fromEntity(document));
        });
    }

    @Override
    public Map<String, Object> updateChunk(String indexName, String knowledgeName, String docId, long chunkId, String content, String modelType) {
        return executeWithResult(() -> {
            EmbeddingProcessor embeddingProcessor = embeddingProcessorFactory.getEmbeddingService(modelType)
                    .orElseThrow(() -> new UnsupportedOperationException("지원하지 않는 임베딩 형식: " + modelType));

            Document document = openSearchChunkRepository.getChunkByChunkId(indexName, knowledgeName, docId, chunkId);

            document.setContent(content);
            document.setContentVec(embeddingProcessor.embed(content));

            String response = openSearchChunkRepository.updateChunkByChunkId(indexName, docId, convertToMap(document));
            return createSuccessResult("문서 청크 수정 성공", response);
        });
    }

    @Override
    public Map<String, Object> deleteChunk(String indexName, String knowledgeName, String docId, long chunkId) {
        return executeWithResult(() -> {
            openSearchChunkRepository.deleteChunkByChunkId(indexName, knowledgeName, docId, chunkId);
            return createSuccessResult("문서 청크 삭제 성공", "");
        });
    }

    private Metadata getAndUpdateMetadata(String indexName, String knowledgeName, String docId) {
        Metadata metadata = openSearchDocumentRepository.getDocMetadataByDocId(indexName, knowledgeName, docId);
        metadata.setChunkNum(metadata.getChunkNum() + 1);
        openSearchDocumentRepository.updateDocMetadataByDocId(indexName, docId, convertToMap(metadata));
        return metadata;
    }

    private Map<String, Object> executeWithResult(ServiceExecutor executor) {
        Map<String, Object> result = new HashMap<>();
        try {
            result.putAll(executor.execute());
        } catch (UnsupportedOperationException e) {
            result.put("status", "error");
            result.put("message", e.getMessage());
        } catch (Exception e) {
            log.error("Error occurred", e);
            result.put("status", "error");
            result.put("message", "오류 발생: " + e.getMessage());
        }
        return result;
    }

    private Map<String, Object> createSuccessResult(String message, Object response) {
        Map<String, Object> result = new HashMap<>();
        result.put("status", "success");
        result.put("message", message);
        result.put("response", response);
        return result;
    }

    @FunctionalInterface
    private interface ServiceExecutor {
        Map<String, Object> execute() throws Exception;
    }
}
