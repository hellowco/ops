package kr.co.proten.llmops.api.document.repository.opensearch;

import kr.co.proten.llmops.api.document.entity.Document;
import kr.co.proten.llmops.api.document.entity.Metadata;
import kr.co.proten.llmops.api.document.repository.DocumentRepository;
import kr.co.proten.llmops.core.aop.OpenSearchConnectAspect;
import kr.co.proten.llmops.core.helpers.DateUtil;
import org.opensearch.client.opensearch.OpenSearchClient;
import org.opensearch.client.opensearch._types.FieldValue;
import org.opensearch.client.opensearch._types.SortOrder;
import org.opensearch.client.opensearch.core.*;
import org.opensearch.client.opensearch.core.search.Hit;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;

/**
 * The type Document repository.
 */
@SuppressWarnings("unchecked")
@Repository
public class OpenSearchDocumentRepository implements DocumentRepository {

    private final Logger log = LoggerFactory.getLogger(this.getClass());

    private static final String FIELD_INDEX = "index";
    private static final String FIELD_DOC_ID = "docId";
    private static final String METADATA = "_metadata";

    @Override
    public List<Metadata> getDocumentList(String indexName, String knowledgeName, int pageNo, int pageSize) {
        final String metaIndexName = indexName + METADATA;


        OpenSearchClient client = OpenSearchConnectAspect.getClient();


        // SearchRequest 생성
        SearchRequest searchRequest = new SearchRequest.Builder()
                .index(metaIndexName)
                .query(q -> q
                        .bool(b -> b
                                .filter(f -> f
                                        .term(t -> t
                                                .field(FIELD_INDEX)
                                                .value(FieldValue.of(knowledgeName))
                                        )
                                )
                        )
                )
                .source(s -> s
                        .filter(f -> f
                                .includes("*")
                        )
                )
                .sort(s -> s
                        .field(sf -> sf
                                .field("index.keyword")
                                .order(SortOrder.Asc) // 오름차순 정렬
                        )
                )
                .from(pageNo * pageSize) // 페이지
                .size(pageSize) // 한 번에 가져올 문서 수
                .build();

        try {
            // 요청 실행
            SearchResponse<Metadata> response = client.search(searchRequest, Metadata.class);

            // 결과 처리
            List<Metadata> metadataList = new ArrayList<>();
            for (Hit<Metadata> hit : response.hits().hits()) {
                metadataList.add(hit.source());
            }

            return metadataList;
        } catch (NoSuchElementException e) { // 검색 실패 잡기
            throw e;
        } catch (Exception e) { // 검색 실패 외 다른 에러 잡기
            throw new RuntimeException("Error retrieving metadata for: " + knowledgeName, e);
        }
    }

    /**
     * 해당하는 문서 번호의 청크된 리스트를 가져오기
     *
     * @param indexName 모델 인덱스 (오픈서치에서의 진짜 인덱스)
     * @param knowledgeName 사용자 인덱스 (index 필드)
     * @param docId     the doc id
     * @param pageNo 페이지 번호
     * @param pageSize 한번에 가져오는 개수
     * @return the doc by doc id
     */
    @Override
    public List<Document> getDocByDocId(String indexName, String knowledgeName, String docId, int pageNo, int pageSize) {

        OpenSearchClient client = OpenSearchConnectAspect.getClient();

        // SearchRequest 생성
        SearchRequest searchRequest = new SearchRequest.Builder()
                .index(indexName)
                .query(q -> q
                        .bool(b -> b
                                .filter(f -> f
                                        .term(t -> t
                                                .field(FIELD_INDEX)
                                                .value(FieldValue.of(knowledgeName))
                                        )
                                )
                                .must(m -> m
                                        .term(t -> t
                                                .field(FIELD_DOC_ID)
                                                .value(FieldValue.of(docId))
                                        )
                                )
                        )
                )
                .source(s -> s
                        .filter(f -> f
                                .includes("*")
                                .excludes("content_vec")
                        )
                )
                .sort(s -> s
                        .field(sf -> sf
                                .field("chunkId")
                                .order(SortOrder.Asc) // 오름차순 정렬
                        )
                )
                .from(pageNo * pageSize) // 페이지
                .size(pageSize) // 한 번에 가져올 문서 수
                .build();

        try {
            // 요청 실행
            SearchResponse<Document> response = client.search(searchRequest, Document.class);

            // 결과 문서 리스트 생성
            List<Document> documents = new ArrayList<>();
            for (Hit<Document> hit : response.hits().hits()) {
                documents.add(hit.source());
            }

            return documents;
        } catch (Exception e) {
            throw new RuntimeException("Error retrieving documents for docId: " + docId, e);
        }
    }

    /**
     * 해당하는 문서 번호의 청크된 리스트를 가져오기
     *
     * @param indexName 모델 인덱스 (오픈서치에서의 진짜 인덱스)
     * @param knowledgeName 사용자 인덱스 (index 필드)
     * @param docId     the doc id
     * @return the doc by doc id
     */
    @Override
    public Metadata getDocMetadataByDocId(String indexName, String knowledgeName, String docId) {

        OpenSearchClient client = OpenSearchConnectAspect.getClient();

        final String metaIndexName = indexName + METADATA;

        // SearchRequest 생성
        SearchRequest searchRequest = new SearchRequest.Builder()
                .index(metaIndexName)
                .query(q -> q
                        .bool(b -> b
                                .filter(f -> f
                                        .term(t -> t
                                                .field(FIELD_INDEX)
                                                .value(FieldValue.of(knowledgeName))
                                        )
                                )
                                .must(m -> m
                                        .term(t -> t
                                                .field(FIELD_DOC_ID)
                                                .value(FieldValue.of(docId))
                                        )
                                )
                        )
                )
                .source(s -> s
                        .filter(f -> f
                                .includes("*")
                        )
                )
                .size(1)
                .build();

        try {
            // 요청 실행
            SearchResponse<Metadata> response = client.search(searchRequest, Metadata.class);

            // 결과 처리
            List<Hit<Metadata>> hits = response.hits().hits();
            if (!hits.isEmpty()) {
                return hits.get(0).source();
            } else {
                throw new NoSuchElementException("No metadata found for docId: " + docId);
            }
        } catch (NoSuchElementException e) { // 검색 실패 잡기
            throw e;
        } catch (Exception e) { // 검색 실패 외 다른 에러 잡기
            throw new RuntimeException("Error retrieving metadata for docId: " + docId, e);
        }
    }

    /**
     * Update doc metadata by doc id.
     *
     * @param indexName     the index name
     * @param docId         the doc id
     * @param updatedFields the updated fields
     */
    @SuppressWarnings("rawtypes")
    public String updateDocMetadataByDocId(String indexName, String docId, Map<String, Object> updatedFields) {

        OpenSearchClient client = OpenSearchConnectAspect.getClient();

        final String metaIndexName = indexName + METADATA;

        // SearchRequest로 docId에 해당하는 메타데이터 찾기
        SearchRequest searchRequest = new SearchRequest.Builder()
                .index(metaIndexName)
                .query(q -> q
                        .bool(b -> b
                                .filter(f -> f
                                        .term(t -> t
                                                .field(FIELD_INDEX)
                                                .value(FieldValue.of(updatedFields.get(FIELD_INDEX).toString()))
                                        )
                                )
                                .must(m -> m
                                        .term(t -> t
                                                .field(FIELD_DOC_ID)
                                                .value(FieldValue.of(docId))
                                        )
                                )
                        )
                )
                .size(1) // 메타데이터는 첫 번째 문서만 처리
                .build();
        try {
            SearchResponse<Map> response = client.search(searchRequest, Map.class);

            if (!response.hits().hits().isEmpty()) {
                // 첫 번째 문서 ID 가져오기
                String documentId = response.hits().hits().get(0).id();

                // 마지막 업데이트 시간 반영
                updatedFields.put("lastUpdatedDate", DateUtil.generateCurrentTimestamp());

                // UpdateRequest 생성
                UpdateRequest updateRequest = new UpdateRequest.Builder()
                        .index(metaIndexName)
                        .id(documentId)
                        .doc(updatedFields)
                        .build();

                // 문서 업데이트
                UpdateResponse updateResponse = client.update(updateRequest, Map.class);

                log.info("Updated document: {}", updateResponse.result());

                return updateResponse.result().toString();
            } else {
                throw new NoSuchElementException("No document found for docId: " + docId);
            }
        } catch (NoSuchElementException e) { // 검색 실패 잡기
            throw e;
        } catch (Exception e) { // 검색 실패 외 다른 에러 잡기
            throw new RuntimeException("Error updating document for docId: " + docId, e);
        }
    }

    /**
     * Delete doc by doc id.
     *
     * @param indexName the index name
     * @param docId     the doc id
     */
    public void deleteDocByDocId(String indexName, String knowledgeName, String docId) {

        OpenSearchClient client = OpenSearchConnectAspect.getClient();

        final String metaIndexName = indexName + METADATA;

        // 인덱스 content 에서 문서 삭제
        DeleteByQueryRequest deleteByQueryRequest1 = new DeleteByQueryRequest.Builder()
                .index(indexName)
                .query(q -> q
                        .bool(b -> b
                                .filter(f -> f
                                        .term(t -> t
                                                .field(FIELD_INDEX)
                                                .value(FieldValue.of(knowledgeName))
                                        )
                                )
                                .must(m -> m
                                        .term(t -> t
                                                .field(FIELD_DOC_ID)
                                                .value(FieldValue.of(docId))
                                        )
                                )
                        )
                )
                .build();

        // 인덱스 metadata 에서 문서 삭제
        DeleteByQueryRequest deleteByQueryRequest2 = new DeleteByQueryRequest.Builder()
                .index(metaIndexName)
                .query(q -> q
                        .bool(b -> b
                                .filter(f -> f
                                        .term(t -> t
                                                .field(FIELD_INDEX)
                                                .value(FieldValue.of(knowledgeName))
                                        )
                                )
                                .must(m -> m
                                        .term(t -> t
                                                .field(FIELD_DOC_ID)
                                                .value(FieldValue.of(docId))
                                        )
                                )
                        )
                )
                .build();

        try {
            // 요청 실행
            DeleteByQueryResponse response1 = client.deleteByQuery(deleteByQueryRequest1);
            DeleteByQueryResponse response2 = client.deleteByQuery(deleteByQueryRequest2);

            // 삭제된 문서 수 출력
            log.info("Deleted {} documents from {}", response1.deleted(), indexName);
            log.info("Deleted {} documents from {}", response2.deleted(), indexName + METADATA);
        } catch (Exception e) {
            throw new RuntimeException("Error deleting documents by query for docId: " + docId, e);
        }
    }

    public boolean saveDocument(String indexName, List<Document> documents, Metadata metadata) {
        boolean isSuccess = false;

        // AOP에서 ThreadLocal을 통해 클라이언트 가져오기
        OpenSearchClient client = OpenSearchConnectAspect.getClient();

        // 메타데이터 인덱스 이름 생성
        final String metaIndexName = indexName + METADATA;

        // Bulk 요청 생성
        BulkRequest.Builder bulkRequestBuilder = new BulkRequest.Builder();

        // 문서들을 Bulk 요청에 추가
        documents.forEach(document ->
            bulkRequestBuilder.operations(op -> op
                    .index(idx -> idx
                            .index(indexName)
                            .id(document.getId())
                            .document(document)
                    )
            )
        );

        // 메타데이터를 Bulk 요청에 추가
        bulkRequestBuilder.operations(op -> op
                .index(idx -> idx
                        .index(metaIndexName)
                        .id(metadata.getId())
                        .document(metadata)
                )
        );

        try {
            // Bulk 요청 실행
            isSuccess = executeBulkRequest(client, bulkRequestBuilder.build());

        } catch (Exception e) {
            throw new RuntimeException("Error saving documents to OpenSearch: ", e);
        }

        return isSuccess;
    }

    private boolean executeBulkRequest(OpenSearchClient client, BulkRequest bulkRequest) {
        try {
            BulkResponse response = client.bulk(bulkRequest);
            if(response.errors()) {
                boolean hasFailures = response.items().stream().anyMatch(item -> item.error() != null);
                if (hasFailures) {
                    StringBuilder errorMessages = new StringBuilder("Bulk request completed with errors: ");
                    response.items().forEach(itemResponse -> {
                        if (itemResponse.error() != null) {
                            errorMessages.append("\nError for operation: ")
                                    .append(itemResponse.id())
                                    .append(" - ")
                                    .append(itemResponse.error().reason());
                        }
                    });
                    throw new RuntimeException(errorMessages.toString());
                }
            }
            return true;
        } catch (Exception e) {
            throw new RuntimeException("Error executing bulk request: ", e);
        }
    }

}
