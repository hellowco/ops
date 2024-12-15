package kr.co.proten.llmops.api.document.repository.opensearch;

import com.fasterxml.jackson.databind.ObjectMapper;
import kr.co.proten.llmops.api.document.entity.Metadata;
import kr.co.proten.llmops.api.document.entity.Document;
import kr.co.proten.llmops.api.document.repository.DocumentRepository;
import kr.co.proten.llmops.core.aop.OpenSearchConnectAspect;
import kr.co.proten.llmops.core.helpers.DateUtil;
import org.opensearch.client.json.JsonpMapper;
import org.opensearch.client.json.jackson.JacksonJsonpMapper;
import org.opensearch.client.opensearch.OpenSearchClient;
import org.opensearch.client.opensearch._types.FieldValue;
import org.opensearch.client.opensearch.core.*;
import org.opensearch.client.opensearch.core.search.Hit;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Repository;

import java.io.StringWriter;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static kr.co.proten.llmops.core.helpers.UUIDGenerator.generateUUID;

/**
 * The type Document repository.
 */
@SuppressWarnings("unchecked")
@Repository
public class OpenSearchDocumentRepository implements DocumentRepository {

    private final Logger log = LoggerFactory.getLogger(this.getClass());

    /**
     * Gets doc id and doc name by index.
     *
     * @param indexName   the index name
     * @param storageName the storage name
     * @return the doc id and doc name by index
     */
    public List<Map<String, String>> getDocIdAndDocNameByIndex(String indexName, String storageName) {
        try {
            OpenSearchClient client = OpenSearchConnectAspect.getClient();

            // SearchRequest 생성
            SearchRequest searchRequest = new SearchRequest.Builder()
                    .index(indexName)
                    .query(q -> q
                            .term(t -> t
                                    .field("index.keyword")
                                    .value(FieldValue.of(storageName))
                            )
                    )
                    .source(s -> s
                            .filter(f -> f.includes("docId", "docName")) // docId와 docName 필드만 반환
                    )
                    .build();

            // 요청 실행
            SearchResponse<Object> response = client.search(searchRequest, Object.class);

            // 결과 추출
            List<Map<String, String>> docList = new ArrayList<>();
            for (Hit<Object> hit : response.hits().hits()) {
                Map<String, Object> source = (Map<String, Object>) hit.source();
                if (source != null) {
                    String docId = source.get("docId").toString();
                    String docName = source.get("docName").toString();
                    docList.add(Map.of("docId", docId, "docName", docName));
                }
            }

            // docList 반환
            return docList;
        } catch (Exception e) {
            throw new RuntimeException("Error retrieving docId and docName list by index: ", e);
        }
    }


    /**
     * Gets doc by doc id.
     *
     * @param indexName the index name
     * @param docId     the doc id
     * @return the doc by doc id
     */
    public List<Document> getDocByDocId(String indexName, String docId) {
        try {
            OpenSearchClient client = OpenSearchConnectAspect.getClient();

            log.info("----------------------------start");

            // SearchRequest 생성
            SearchRequest searchRequest = new SearchRequest.Builder()
                    .index(indexName)
                    .query(q -> q
                            .term(t -> t
                                    .field("docId")
                                    .value(FieldValue.of(docId))
                            )
                    )
                    .source(s -> s
                            .filter(f -> f
                                    .includes("*")
                                    .excludes("content_vec")
                            ))
                    .build();

            // 요청 실행
            SearchResponse<Object> response = client.search(searchRequest, Object.class);
//            SearchResponse<Document> response = client.search(searchRequest, Document.class);

//            // 결과 문서 리스트 생성
//            List<Document> documents = new ArrayList<>();
//            for (Hit<Object> hit : response.hits().hits()) {
//                log.info("hit response:{}", hit.source().toString());
//                log.info("hit response:{}", hit.source().getClass().getTypeName());
//                documents.add((Document) hit.source());
//            }

            ObjectMapper objectMapper = new ObjectMapper();

            List<Document> documents = response.hits().hits().stream()
                    .map(hit -> {
                        LinkedHashMap<String, Object> sourceMap = (LinkedHashMap<String, Object>) hit.source();
                        return objectMapper.convertValue(sourceMap, Document.class);
                    })
                    .collect(Collectors.toList());

            return documents;
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("Error retrieving documents for docId: " + docId, e);
        }
    }

    /**
     * Update doc metadata by doc id.
     *
     * @param indexName     the index name
     * @param docId         the doc id
     * @param updatedFields the updated fields
     */
// updateDocByDocId
    @SuppressWarnings("rawtypes")
    public String updateDocMetadataByDocId(String indexName, String docId, Map<String, Object> updatedFields) {
        try {
            OpenSearchClient client = OpenSearchConnectAspect.getClient();

            // SearchRequest로 docId에 해당하는 메타데이터 찾기
            SearchRequest searchRequest = new SearchRequest.Builder()
                    .index(indexName)
                    .query(q -> q
                            .term(t -> t
                                    .field("docId")
                                    .value(FieldValue.of(docId))
                            )
                    )
                    .size(1) // 메타데이터는 첫 번째 문서만 처리
                    .build();

            SearchResponse<Map> response = client.search(searchRequest, Map.class);

            if (!response.hits().hits().isEmpty()) {
                // 첫 번째 문서 ID 가져오기
                String documentId = response.hits().hits().get(0).id();

                // 마지막 업데이트 시간 반영
                updatedFields.put("lastUpdateDate", DateUtil.generateCurrentTimestamp());

                // UpdateRequest 생성
                UpdateRequest updateRequest = new UpdateRequest.Builder()
                        .index(indexName)
                        .id(documentId)
                        .doc(updatedFields)
                        .build();

                // 문서 업데이트
                UpdateResponse updateResponse = client.update(updateRequest, Map.class);

                log.info("Updated document: {}", updateResponse.result());

                return updateResponse.result().toString();
            } else {
                log.info("No document found for docId: {}", docId);
            }
        } catch (Exception e) {
            throw new RuntimeException("Error updating document for docId: " + docId, e);
        }
        return null;
    }

    /**
     * Delete doc by doc id.
     *
     * @param indexName the index name
     * @param docId     the doc id
     */
// DeleteDocByDocId
    public void deleteDocByDocId(String indexName, String docId) {
        try {
            OpenSearchClient client = OpenSearchConnectAspect.getClient();

            final String metaIndexName = indexName + "_metadata";

            // 인덱스 content 에서 문서 삭제
            DeleteByQueryRequest deleteByQueryRequest1 = new DeleteByQueryRequest.Builder()
                    .index(indexName)
                    .query(q -> q
                            .term(t -> t
                                    .field("docId")  // 필터링할 필드
                                    .value(FieldValue.of(docId))    // 삭제할 docId 값
                            )
                    )
                    .build();

            // 인덱스 metadata 에서 문서 삭제
            DeleteByQueryRequest deleteByQueryRequest2 = new DeleteByQueryRequest.Builder()
                    .index(metaIndexName)
                    .query(q -> q
                            .term(t -> t
                                    .field("docId")  // 필터링할 필드
                                    .value(FieldValue.of(docId))    // 삭제할 docId 값
                            )
                    )
                    .build();

            // 요청 실행
            DeleteByQueryResponse response1 = client.deleteByQuery(deleteByQueryRequest1);
            DeleteByQueryResponse response2 = client.deleteByQuery(deleteByQueryRequest2);

            // 삭제된 문서 수 출력
            log.info("Deleted {} documents from {}", response1.deleted(), indexName);
            log.info("Deleted {} documents from {}", response2.deleted(), indexName+"_metadata");
        } catch (Exception e) {
            throw new RuntimeException("Error deleting documents by query for docId: " + docId, e);
        }
    }


    public boolean saveDocument(String indexName, List<Document> documents, Metadata metadata) {
        boolean isSuccess = false;
        try {
            // AOP에서 ThreadLocal을 통해 클라이언트 가져오기
            OpenSearchClient client = OpenSearchConnectAspect.getClient();

            // 메타데이터 인덱스 이름 생성
            final String metaIndexName = indexName + "_metadata";

            // Bulk 요청 생성
            BulkRequest.Builder bulkRequestBuilder = new BulkRequest.Builder();

            // 문서들을 Bulk 요청에 추가
            documents.forEach(document -> {
                bulkRequestBuilder.operations(op -> op
                        .index(idx -> idx
                                .index(indexName)
                                .id(generateUUID())
                                .document(document)));
            });

            // 메타데이터를 Bulk 요청에 추가
            bulkRequestBuilder.operations(op -> op
                    .index(idx -> idx
                            .index(metaIndexName)
                            .id(generateUUID())
                            .document(metadata)));

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
