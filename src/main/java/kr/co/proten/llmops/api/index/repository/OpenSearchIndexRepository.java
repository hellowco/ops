package kr.co.proten.llmops.api.index.repository;

import kr.co.proten.llmops.core.aop.OpenSearchConnectAspect;
import org.opensearch.client.opensearch.OpenSearchClient;
import org.opensearch.client.opensearch._types.mapping.Property;
import org.opensearch.client.opensearch._types.mapping.TypeMapping;
import org.opensearch.client.opensearch.indices.CreateIndexRequest;
import org.opensearch.client.opensearch.indices.DeleteIndexRequest;
import org.opensearch.client.opensearch.indices.IndexSettings;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Repository;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.Map;

import static kr.co.proten.llmops.core.helpers.MappingLoader.convertToProperties;

@Repository
public class OpenSearchIndexRepository {

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

    public void deleteIndex(String indexName) throws IOException {
        // AOP에서 ThreadLocal을 통해 클라이언트 가져오기
        OpenSearchClient client = OpenSearchConnectAspect.getClient();

        DeleteIndexRequest deleteIndexRequest = new DeleteIndexRequest.Builder()
                .index(indexName)
                .build();
        client.indices().delete(deleteIndexRequest);
        log.info("Index deleted: {}", indexName);
    }
}
