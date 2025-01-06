package kr.co.proten.llmops.api.search.repository;

import jakarta.json.stream.JsonGenerator;
import kr.co.proten.llmops.api.document.entity.Document;
import kr.co.proten.llmops.core.aop.OpenSearchConnectAspect;
import org.opensearch.client.json.jackson.JacksonJsonpMapper;
import org.opensearch.client.opensearch.OpenSearchClient;
import org.opensearch.client.opensearch._types.FieldValue;
import org.opensearch.client.opensearch._types.query_dsl.Operator;
import org.opensearch.client.opensearch._types.query_dsl.Query;
import org.opensearch.client.opensearch.core.SearchRequest;
import org.opensearch.client.opensearch.core.SearchResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Repository;

import java.io.StringWriter;
import java.util.List;
import java.util.stream.Collectors;

@Repository
public class SearchRepository {
    private final Logger log = LoggerFactory.getLogger(this.getClass());

    public List<Document> keywordSearch(String indexName, String knowledgeName,String query) {

        OpenSearchClient client = OpenSearchConnectAspect.getClient();

        Query simpleQueryStringQuery = Query.of(q -> q
                .bool(b -> b
                        .must(List.of( // Ensure `must` is an array
                                Query.of(mq -> mq
                                        .queryString(sqs -> sqs
                                                .query(query)
                                                .fields("content^2.0", "content.exact^2.0") // Pass fields as a list
                                                .defaultOperator(Operator.And)
                                                .analyzeWildcard(true)
                                        )
                                )
                        ))
                        .filter(List.of( // Ensure `filter` is also an array
                                Query.of(fq -> fq
                                        .term(t -> t
                                                .field("index")
                                                .value(FieldValue.of(knowledgeName))
                                        )
                                ),
                                Query.of(fq -> fq
                                        .term(t -> t
                                                .field("isActive")
                                                .value(FieldValue.of(true))
                                        )
                                )
                        ))
                )
        );

        SearchRequest searchRequest = SearchRequest.of(r -> r
                .index(indexName)
                .query(simpleQueryStringQuery)
                .source(s -> s
                        .filter(f -> f
                                .includes("*")
                                .excludes("content_vec")
                        )
                )
        );

        log.info("search request: {}", toJson(searchRequest));

        try {
            // 요청 실행
            SearchResponse<Document> response = client.search(searchRequest, Document.class);

            // 결과 문서 리스트 생성
            return response.hits().hits().stream()
                    .map(hit -> {
                        Document document = hit.source();
                        document.setScore(hit.score()); // 점수 설정
                        return document;
                    })
                    .collect(Collectors.toList());
        } catch (Exception e) {
            throw new RuntimeException("Error while retrieving documents for: " + query, e);
        }
    }

    public List<Document> vectorSearch(String indexName, String knowledgeName, float[] query, int k) {

        OpenSearchClient client = OpenSearchConnectAspect.getClient();

        // 1. KnnQuery 생성
        Query knnQuery = Query.of(q -> q
                .knn(knn -> knn
                        .field("content_vec")
                        .vector(query)
                        .k(k)
                )
        );

        // 2. Filter 조건 생성
        List<Query> filters = List.of(
                Query.of(fq -> fq
                        .term(t -> t
                                .field("index")
                                .value(FieldValue.of(knowledgeName))
                        )
                ),
                Query.of(fq -> fq
                        .term(t -> t
                                .field("isActive")
                                .value(FieldValue.of(true))
                        )
                )
        );

        // 3. Bool 쿼리 생성
        Query boolQuery = Query.of(bq -> bq
                .bool(bool -> bool
                        .should(knnQuery) // KnnQuery를 must 조건에 추가
                        .filter(filters) // 필터 조건 추가
                )
        );

        // 4. SearchRequest 생성
        SearchRequest searchRequest = SearchRequest.of(sr -> sr
                .index(indexName)
                .query(boolQuery)
                .source(s -> s
                        .filter(f -> f
                                .includes("*")
                                .excludes("content_vec")
                        )
                )
        );

        log.info("search request: {}", toJson(searchRequest));

        try {
            // 요청 실행
            SearchResponse<Document> response = client.search(searchRequest, Document.class);

            // 결과 문서 리스트 생성
            return response.hits().hits().stream()
                    .map(hit -> {
                        Document document = hit.source();
                        document.setScore(hit.score()); // 점수 설정
                        return document;
                    })
                    .toList();
        } catch (Exception e) {
            throw new RuntimeException("Error while retrieving documents for: ", e);
        }
    }

    public static String toJson(SearchRequest searchRequest) {
        try (StringWriter writer = new StringWriter()) {
            JacksonJsonpMapper mapper = new JacksonJsonpMapper();

            JsonGenerator generator = mapper.jsonProvider().createGenerator(writer);
            searchRequest.serialize(generator, mapper); // SearchRequest 객체를 직렬화
            generator.close();

            return writer.toString();
        } catch (Exception e) {
            throw new RuntimeException("SearchRequest를 JSON으로 변환하는 데 실패했습니다.", e);
        }
    }

}
