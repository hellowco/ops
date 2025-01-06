package kr.co.proten.llmops.api.document.repository.opensearch;

import kr.co.proten.llmops.api.document.entity.Document;
import kr.co.proten.llmops.api.document.repository.ChunkRepository;
import kr.co.proten.llmops.core.aop.OpenSearchConnectAspect;
import kr.co.proten.llmops.core.helpers.DateUtil;
import org.opensearch.client.opensearch.OpenSearchClient;
import org.opensearch.client.opensearch._types.FieldValue;
import org.opensearch.client.opensearch._types.Result;
import org.opensearch.client.opensearch.core.*;
import org.opensearch.client.opensearch.core.search.Hit;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Repository;

import java.util.*;

import static kr.co.proten.llmops.core.helpers.MappingLoader.convertToMap;

@Repository
public class OpenSearchChunkRepository implements ChunkRepository {
    private static final Logger log = LoggerFactory.getLogger(OpenSearchChunkRepository.class);

    private static final String FIELD_INDEX = "index";
    private static final String FIELD_DOC_ID = "docId";
    private static final String FIELD_CHUNK_ID = "chunkId";

    @Override
    public Document saveChunk(String indexName, Document document) {
        return handleOpenSearchOperation(() -> {
            OpenSearchClient client = OpenSearchConnectAspect.getClient();

            IndexRequest<Document> request = new IndexRequest.Builder<Document>()
                    .index(indexName)
                    .id(document.getId())
                    .document(document)
                    .build();

            IndexResponse response = client.index(request);
            validateResponse(response.result(), "save document");
            updateMetadata(indexName, convertToMap(document), client);

            return document;
        }, "Error saving chunk");
    }

    @Override
    public Document getChunkByChunkId(String indexName, String knowledgeName, String docId, long chunkId) {
        return handleOpenSearchOperation(() -> {
            SearchRequest request = buildSearchRequest(indexName, knowledgeName, docId, chunkId);

            return executeSearch(request).stream()
                    .findFirst()
                    .orElseThrow(() -> new NoSuchElementException("No document found for chunkId: " + chunkId));
        }, "Error retrieving chunk");
    }

    @Override
    @SuppressWarnings("unchecked")
    public String updateChunkByChunkId(String indexName, String docId, Map<String, Object> updatedFields) {
        return handleOpenSearchOperation(() -> {
            OpenSearchClient client = OpenSearchConnectAspect.getClient();

            String documentId = findChunkId(indexName, updatedFields);
            log.info("documentId: {}", documentId);

            // UpdateRequest 생성: 제네릭 타입 명시
            UpdateRequest<Map<String, Object>, Map<String, Object>> request = new UpdateRequest.Builder<Map<String, Object>, Map<String, Object>>()
                    .index(indexName)
                    .id(documentId)
                    .doc(updatedFields)
                    .build();

            // 클라이언트 업데이트 실행
            UpdateResponse<Map<String, Object>> response = client.update(request, (Class<Map<String, Object>>) (Class<?>) Map.class);

            // 결과 검증
            if (response.result() != Result.Updated && response.result() != Result.NoOp) {
                throw new RuntimeException("Failed to update chunk. Result: " + response.result());
            }

            log.info("Chunk successfully updated: {}", response.result());
            validateResponse(response.result(), "update document");
            updateMetadata(indexName, updatedFields, client);

            return response.result().toString();
        }, "Error updating chunk");
    }

    @Override
    public void deleteChunkByChunkId(String indexName, String knowledgeName, String docId, long chunkId) {
        handleOpenSearchOperation(() -> {
            OpenSearchClient client = OpenSearchConnectAspect.getClient();
            DeleteByQueryRequest request = buildDeleteRequest(indexName, knowledgeName, docId, chunkId);

            DeleteByQueryResponse response = client.deleteByQuery(request);
            log.info("Deleted {} documents from {}", response.deleted(), indexName);

            return null;
        }, "Error deleting chunk");
    }

    private SearchRequest buildSearchRequest(String indexName, String knowledgeName, String docId, long chunkId) {
        return new SearchRequest.Builder()
                .index(indexName)
                .query(q -> q.bool(b -> b
                        .must(m -> m.term(t -> t.field(FIELD_INDEX).value(FieldValue.of(knowledgeName))))
                        .must(m -> m.term(t -> t.field(FIELD_DOC_ID).value(FieldValue.of(docId))))
                        .must(m -> m.term(t -> t.field(FIELD_CHUNK_ID).value(FieldValue.of(chunkId))))))
                .size(1)
                .source(s -> s
                        .filter(f -> f
                                .includes("*")
                                .excludes("content_vec")
                        )
                )
                .build();
    }

    private DeleteByQueryRequest buildDeleteRequest(String indexName, String knowledgeName, String docId, long chunkId) {
        return new DeleteByQueryRequest.Builder()
                .index(indexName)
                .query(q -> q.bool(b -> b
                        .must(m -> m.term(t -> t.field(FIELD_INDEX).value(FieldValue.of(knowledgeName))))
                        .must(m -> m.term(t -> t.field(FIELD_DOC_ID).value(FieldValue.of(docId))))
                        .must(m -> m.term(t -> t.field(FIELD_CHUNK_ID).value(FieldValue.of(chunkId))))))
                .build();
    }

    private List<Document> executeSearch(SearchRequest request) throws Exception {
        OpenSearchClient client = OpenSearchConnectAspect.getClient();
        SearchResponse<Document> response = client.search(request, Document.class);

        List<Document> documents = new ArrayList<>();
        for (Hit<Document> hit : response.hits().hits()) {
            documents.add(hit.source());
        }

        return documents;
    }

    private String findChunkId(String indexName, Map<String, Object> updatedFields) throws Exception {
        SearchRequest searchRequest = buildSearchRequest(indexName, updatedFields.get(FIELD_INDEX).toString(),
                updatedFields.get(FIELD_DOC_ID).toString(), Long.parseLong(updatedFields.get(FIELD_CHUNK_ID).toString()));

        return executeSearch(searchRequest).get(0).getId();
    }

    @SuppressWarnings("unchecked")
    private void updateMetadata(String indexName, Map<String, Object> updatedFields, OpenSearchClient client) throws Exception {
        String metadataIndex = indexName + "_metadata";
        String docId = updatedFields.get("docId").toString();

        // 메타데이터 문서의 ID를 조회
        String metadataId = findMetadataId(client, metadataIndex, docId);

        if (metadataId == null) {
            throw new NoSuchElementException("Metadata document not found for docId: " + docId);
        }

        // UpdateRequest 생성
        UpdateRequest<Map<String, Object>, Map<String, Object>> request = new UpdateRequest.Builder<Map<String, Object>, Map<String, Object>>()
                .index(metadataIndex)
                .id(metadataId)
                .doc(Collections.singletonMap("lastUpdatedDate", DateUtil.generateCurrentTimestamp()))
                .build();

        // OpenSearch 업데이트 실행
        UpdateResponse<Map<String, Object>> response = client.update(request, (Class<Map<String, Object>>) (Class<?>) Map.class);

        // 결과 검증
        if (response.result() != Result.Updated && response.result() != Result.NoOp) {
            throw new RuntimeException("Metadata update failed. Result: " + response.result());
        }
        log.info("Metadata successfully updated: {}", response.result());
    }

    // 메타데이터 인덱스에서 docId로 문서 ID를 조회하는 메서드
    @SuppressWarnings("unchecked")
    private String findMetadataId(OpenSearchClient client, String metadataIndex, String docId) throws Exception {
        SearchRequest searchRequest = new SearchRequest.Builder()
                .index(metadataIndex)
                .query(q -> q
                        .term(t -> t
                                .field("docId")
                                .value(FieldValue.of(docId))
                        )
                )
                .size(1) // 첫 번째 결과만 필요`
                .build();

        SearchResponse<Map<String, Object>> response = client.search(searchRequest, (Class<Map<String, Object>>) (Class<?>) Map.class);

        if (!response.hits().hits().isEmpty()) {
            return response.hits().hits().get(0).id(); // 첫 번째 문서의 ID 반환
        } else {
            log.warn("No metadata document found for docId: {}", docId);
            return null;
        }
    }

    private void validateResponse(Result result, String operation) {
        if (result != Result.Created && result != Result.Updated) {
            throw new RuntimeException(operation + " failed: " + result);
        }
    }

    private <T> T handleOpenSearchOperation(CheckedSupplier<T> supplier, String errorMessage) {
        try {
            return supplier.get();
        } catch (Exception e) {
            log.error(errorMessage, e);
            throw new RuntimeException(errorMessage, e);
        }
    }

    @FunctionalInterface
    private interface CheckedSupplier<T> {
        T get() throws Exception;
    }
}
