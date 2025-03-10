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
import org.opensearch.client.opensearch.core.search.Highlight;
import org.opensearch.client.opensearch.core.search.HighlightField;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Repository;

import java.io.StringWriter;
import java.util.List;
import java.util.stream.Collectors;

@Repository
public class SearchRepository {
    private final Logger log = LoggerFactory.getLogger(this.getClass());

    public List<Document> keywordSearch(String indexName, String knowledgeName,String query, int page, int pageSize) {

        OpenSearchClient client = OpenSearchConnectAspect.getClient();

        String escapedQuery = escapeLuceneSpecialChars(query);

        Query simpleQueryStringQuery = Query.of(q -> q
                .bool(b -> b
                        .must(List.of( // Ensure `must` is an array
                                Query.of(mq -> mq
                                        .queryString(sqs -> sqs
                                                .query(escapedQuery)
                                                .analyzer("pro10_kr_noun") //자연어에서 명사만 추출
                                                .fields("content^2.0", "content.exact^2.0") // Pass fields as a list
                                                .defaultOperator(Operator.Or) // 자연어인 경우, and면 결과가 안나올수 있음
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

        Highlight highlight = Highlight.of(h -> h
                .fields("content", HighlightField.of(f -> f
                        .preTags("<em>")  // 하이라이트 시작 태그
                        .postTags("</em>") // 하이라이트 종료 태그
                ))
                .fields("content.exact", HighlightField.of(f -> f
                        .preTags("<em>")
                        .postTags("</em>")
                ))
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
                .highlight(highlight)
                .size(pageSize)
                .from(page * pageSize)
        );

        log.debug("keyword search request: {}", toJson(searchRequest));

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

    public List<Document> vectorSearch(String indexName, String knowledgeName, float[] query, int k, int page, int pageSize) {

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
                .size(pageSize)
                .from(page * pageSize)
        );

        log.debug("vector search request: {}", toJson(searchRequest));

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

    /**
     * Lucene 예약어 특수문자(!, (, ), ^)를 이스케이프 처리하는 메서드.
     *
     * @param input 원본 쿼리 문자열
     * @return 이스케이프 처리된 문자열
     */
    private static String escapeLuceneSpecialChars(String input) {
        if (input == null) {
            return null;
        }
        // 예약어 특수문자 목록: !, (, ), ^
        // 정규 표현식을 사용하여 해당 문자 앞에 백슬래시 추가
        return input.replaceAll("([!()^])", "\\\\$1");
    }

}
