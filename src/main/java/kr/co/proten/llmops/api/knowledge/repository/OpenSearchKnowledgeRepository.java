package kr.co.proten.llmops.api.knowledge.repository;

import kr.co.proten.llmops.api.knowledge.entity.Knowledge;
import kr.co.proten.llmops.core.aop.OpenSearchConnectAspect;
import org.opensearch.client.opensearch.OpenSearchClient;
import org.opensearch.client.opensearch._types.FieldValue;
import org.opensearch.client.opensearch._types.mapping.TypeMapping;
import org.opensearch.client.opensearch.core.*;
import org.opensearch.client.opensearch.core.search.Hit;
import org.opensearch.client.opensearch.indices.CreateIndexRequest;
import org.opensearch.client.opensearch.indices.DeleteIndexRequest;
import org.opensearch.client.opensearch.indices.IndexSettings;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Repository;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Repository
public class OpenSearchKnowledgeRepository {

    private final Logger log = LoggerFactory.getLogger(this.getClass());

    public void createIndex(String indexName, Map<String, Object> mapping) throws IOException {
        // AOP에서 ThreadLocal을 통해 클라이언트 가져오기
        OpenSearchClient client = OpenSearchConnectAspect.getClient();

        // 맵핑 정보 추출
//        @SuppressWarnings("unchecked")
//        Map<String, Object> mappings = (Map<String, Object>) mapping.get("mappings");
//        @SuppressWarnings("unchecked")
//        Map<String, Object> rawProperties = (Map<String, Object>) mappings.get("properties");

        // 설정 정보 추출
        @SuppressWarnings("unchecked")
//        Map<String, Object> settings = (Map<String, Object>) mapping.get("settings");

//        Map<String, Property> properties = convertToProperties(rawProperties);

        // 설정 변환
        IndexSettings indexSettings = (IndexSettings) mapping.get("settings");

        TypeMapping typeMapping = (TypeMapping) mapping.get("mappings");

        try {

//        TypeMapping typeMapping = new TypeMapping.Builder()
//                .properties(properties)
//                .build();

        CreateIndexRequest createIndexRequest = new CreateIndexRequest.Builder()
                .index(indexName)
                .mappings(typeMapping)
                .settings(indexSettings)
                .build();
        client.indices().create(createIndexRequest);
        log.info("Index created: {}", indexName);
        }catch (Exception e){
            e.printStackTrace();
        }

    }

    /**
     * Gets doc id and doc name by index.
     *
     * @param indexName   the index name
     * @param knowledgeName the storage name
     * @return the doc id and doc name by index
     */
    public List<Map<String, String>> getDocIdAndDocNameByIndex(String indexName, String knowledgeName) {

        OpenSearchClient client = OpenSearchConnectAspect.getClient();

        // SearchRequest 생성
        SearchRequest searchRequest = new SearchRequest.Builder()
                .index(indexName)
                .query(q -> q
                        .term(t -> t
                                .field("index.keyword")
                                .value(FieldValue.of(knowledgeName))
                        )
                )
                .source(s -> s
                        .filter(f -> f.includes("docId", "docName")) // docId와 docName 필드만 반환
                )
                .build();

        try {
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

    public void deleteIndex(String indexName) {
        // AOP에서 ThreadLocal을 통해 클라이언트 가져오기
        OpenSearchClient client = OpenSearchConnectAspect.getClient();

        DeleteIndexRequest deleteIndexRequest = new DeleteIndexRequest.Builder()
                .index(indexName)
                .build();

        try {
            client.indices().delete(deleteIndexRequest);
            log.info("Index deleted: {}", indexName);
        } catch (Exception e) {
            throw new RuntimeException("Error while deleting index: ", e);
        }
    }

    public List<Knowledge> findAllKnowledge(String indexName) throws IOException {
        // AOP에서 ThreadLocal을 통해 클라이언트 가져오기
        OpenSearchClient client = OpenSearchConnectAspect.getClient();

        SearchRequest searchRequest = new SearchRequest.Builder()
                .index(indexName)
                .query(q -> q.matchAll(m -> m))
                .build();

        try{
            SearchResponse<Knowledge> response = client.search(searchRequest, Knowledge.class);

            return response.hits().hits().stream()
                    .map(Hit::source)
                    .toList();
        } catch (Exception e) {
            throw new RuntimeException("Error retrieving knowledge list: ", e);
        }
    }

    public String saveKnowledge(String indexName, Knowledge entity) throws IOException {
        // AOP에서 ThreadLocal을 통해 클라이언트 가져오기
        OpenSearchClient client = OpenSearchConnectAspect.getClient();

        IndexRequest<Knowledge> indexRequest = new IndexRequest.Builder<Knowledge>()
                .id(entity.getId())
                .index(indexName)
                .document(entity)
                .build();

        try {
            IndexResponse response = client.index(indexRequest);

            return response.id();
        } catch (Exception e) {
            throw new RuntimeException("Error while saving knowledge: ", e);
        }
    }

    public Knowledge findById(String indexName, String id) throws IOException {
        // AOP에서 ThreadLocal을 통해 클라이언트 가져오기
        OpenSearchClient client = OpenSearchConnectAspect.getClient();

        GetRequest getRequest = new GetRequest.Builder()
                .index(indexName)
                .id(id)
                .build();

        try {
            GetResponse<Knowledge> response = client.get(getRequest, Knowledge.class);

            if (response.found()) {
                return response.source();
            } else {
                return null; // 문서를 찾지 못한 경우 null 반환
            }
        } catch (Exception e) {
            throw new RuntimeException("Error retrieving knowledge by id: ", e);
        }
    }

    public String updateKnowledge(String indexName, String id, Knowledge entity) throws IOException {
        // AOP에서 ThreadLocal을 통해 클라이언트 가져오기
        OpenSearchClient client = OpenSearchConnectAspect.getClient();

        UpdateRequest<Knowledge, Knowledge> updateRequest = new UpdateRequest.Builder<Knowledge, Knowledge>()
                .index(indexName)
                .id(id)
                .doc(entity) // 수정된 내용 적용
                .build();

        try{
            UpdateResponse<Knowledge> response = client.update(updateRequest, Knowledge.class);
            return response.id(); // 업데이트된 문서 ID 반환
        } catch (Exception e) {
            throw new RuntimeException("Error while updating knowledge: ", e);
        }
    }

    public String deleteKnowledge(String indexName, String id) throws IOException {
        // AOP에서 ThreadLocal을 통해 클라이언트 가져오기
        OpenSearchClient client = OpenSearchConnectAspect.getClient();

        DeleteRequest deleteRequest = new DeleteRequest.Builder()
                .index(indexName)
                .id(id)
                .build();

        try {
            DeleteResponse response = client.delete(deleteRequest);
            return response.id();
        } catch (Exception e) {
            throw new RuntimeException("Error while deleting knowledge: ", e);
        }
    }

}