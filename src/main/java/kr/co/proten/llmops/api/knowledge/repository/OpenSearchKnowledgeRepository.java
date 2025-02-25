package kr.co.proten.llmops.api.knowledge.repository;

import kr.co.proten.llmops.api.knowledge.entity.Knowledge;
import kr.co.proten.llmops.core.aop.OpenSearchConnectAspect;
import kr.co.proten.llmops.core.exception.IndexCreationException;
import org.apache.http.entity.ContentType;
import org.apache.http.nio.entity.NStringEntity;
import org.apache.http.util.EntityUtils;
import org.opensearch.client.Request;
import org.opensearch.client.Response;
import org.opensearch.client.RestClient;
import org.opensearch.client.opensearch.OpenSearchClient;
import org.opensearch.client.opensearch._types.FieldValue;
import org.opensearch.client.opensearch.core.*;
import org.opensearch.client.opensearch.core.search.Hit;
import org.opensearch.client.opensearch.indices.DeleteIndexRequest;
import org.opensearch.client.transport.rest_client.RestClientTransport;
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

    public boolean createIndex(String indexName, int dimension) {
        // AOP에서 ThreadLocal을 통해 클라이언트 가져오기
        OpenSearchClient client = OpenSearchConnectAspect.getClient();

        String mappingJson = getMappingJson(dimension);

        // OpenSearchClient 내부의 low-level RestClient 추출
        RestClient restClient = ((RestClientTransport) client._transport()).restClient();

        // Request 객체 생성: HTTP 메서드와 엔드포인트 지정
        Request request = new Request("PUT", "/" + indexName);
        // JSON 본문 설정
        request.setEntity(new NStringEntity(mappingJson, ContentType.APPLICATION_JSON));

        // 요청 전송 및 응답 받기
        Response response;
        try {
            response = restClient.performRequest(request);
            log.info("Index Creation Response code: {}", response.getStatusLine().getStatusCode());
            String responseBody = EntityUtils.toString(response.getEntity());
            log.info("Index Creation Response body: {}", responseBody);
        } catch (IOException e) {
            throw new IndexCreationException("Error while creating index: " + indexName);
        }

        return response.getStatusLine().getStatusCode() == 200;
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

        //TODO:: 지식 인덱스에서 id로 현재 삭제할 지식의 모델명 가져와서
        // 모델명의 인덱스에서 지식명을 가지는 모든 정보를 지워야함
        // 모델명 인덱스와 모델명_메타데이터 인덱스에서 해당하는 지식명을 가지는 것을 지워야함


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

    private static String getMappingJson(int dimension) {
        return """
                {
                  "mappings": {
                    "properties": {
                      "@timestamp": {
                        "type": "date"
                      },
                      "id": {
                        "type": "keyword",
                        "ignore_above": 256
                      },
                      "docId": {
                        "type": "keyword",
                        "ignore_above": 256
                      },
                      "chunkId": {
                        "type": "long"
                      },
                      "knowledgeName": {
                        "type": "text",
                        "fields": {
                          "keyword": {
                            "type": "keyword",
                            "ignore_above": 256
                          }
                        }
                      },
                      "isActive": {
                        "type": "boolean"
                      },
                      "content": {
                        "type": "text",
                        "fields": {
                          "exact": {
                            "type": "text",
                            "analyzer": "standard_analyzer"
                          }
                        },
                        "analyzer": "pro10_kr",
                        "search_analyzer": "pro10_search"
                      },
                      "content_vec": {
                        "type": "knn_vector",
                        "dimension": %d,
                        "method": {
                          "engine": "faiss",
                          "space_type": "innerproduct",
                          "name": "hnsw",
                          "parameters": {
                            "ef_construction": 512,
                            "m": 64
                          }
                        }
                      },
                      "page": {
                        "type": "long"
                      }
                    }
                  },
                  "settings": {
                    "index": {
                      "similarity": {
                        "default": {
                          "type": "BM25",
                          "b": 0.0,
                          "k1": 1.2
                        }
                      },
                      "number_of_shards": 5,
                      "number_of_replicas": 1,
                      "max_ngram_diff": 20,
                      "max_result_window": 1000000,
                      "max_inner_result_window": 1000,
                      "blocks": {
                        "read_only_allow_delete": null
                      }
                    },
                    "knn": "true",
                    "analysis": {
                      "filter": {
                        "edge_filter": {
                          "type": "edge_ngram",
                          "min_gram": "1",
                          "max_gram": "20"
                        },
                        "ngram_filter": {
                          "type": "ngram",
                          "min_gram": "2",
                          "max_gram": "20"
                        }
                      },
                      "char_filter": {
                        "remove_whitespace_filter": {
                          "pattern": " ",
                          "type": "pattern_replace",
                          "replacement": ""
                        },
                        "remove_filter": {
                          "pattern": "[_]",
                          "type": "pattern_replace",
                          "replacement": " "
                        },
                        "replace_special_char_filter": {
                          "pattern": "[^가-힣xfe0-9a-zA-Z\\\\s]",
                          "type": "pattern_replace",
                          "replacement": " "
                        },
                        "remove_special_char_filter": {
                          "pattern": "[^가-힣xfe0-9a-zA-Z\\\\s]",
                          "type": "pattern_replace",
                          "replacement": ""
                        }
                      },
                      "analyzer": {
                        "edge_analyzer": {
                          "filter": [
                            "lowercase"
                          ],
                          "tokenizer": "edge_ngram"
                        },
                        "ngram_analyzer": {
                          "filter": [
                            "lowercase",
                            "ngram_filter"
                          ],
                          "tokenizer": "my_whitespace"
                        },
                        "category_analyzer": {
                          "tokenizer": "category_tokenizer"
                        },
                        "reverse_ngram_analyzer": {
                          "filter": [
                            "lowercase",
                            "reverse",
                            "edge_filter",
                            "reverse"
                          ],
                          "tokenizer": "standard"
                        },
                        "reverse_ngram_sc_ws_analyzer": {
                          "filter": [
                            "lowercase",
                            "reverse",
                            "edge_filter",
                            "reverse"
                          ],
                          "char_filter": [
                            "remove_special_char_filter",
                            "remove_whitespace_filter"
                          ],
                          "tokenizer": "standard"
                        },
                        "bigram_search_analyzer": {
                          "filter": [
                            "lowercase"
                          ],
                          "tokenizer": "standard"
                        },
                        "front_ngram_sc_ws_analyzer": {
                          "filter": [
                            "lowercase",
                            "edge_filter"
                          ],
                          "char_filter": [
                            "remove_special_char_filter",
                            "remove_whitespace_filter"
                          ],
                          "tokenizer": "standard"
                        },
                        "bigram_analyzer": {
                          "filter": [
                            "lowercase"
                          ],
                          "tokenizer": "my_bigram"
                        },
                        "pattern_analyzer": {
                          "filter": [
                            "lowercase"
                          ],
                          "tokenizer": "my_pattern"
                        },
                        "keyword_analyzer": {
                          "filter": [
                            "lowercase"
                          ],
                          "char_filter": [
                            "remove_special_char_filter",
                            "remove_whitespace_filter"
                          ],
                          "tokenizer": "my_keyword"
                        },
                        "front_ngram_analyzer": {
                          "filter": [
                            "lowercase",
                            "edge_filter"
                          ],
                          "tokenizer": "standard"
                        },
                        "whitespace_analyzer": {
                          "filter": [
                            "lowercase",
                            "trim"
                          ],
                          "tokenizer": "my_whitespace"
                        },
                        "standard_analyzer": {
                          "type": "custom",
                          "filter": [
                            "lowercase",
                            "trim"
                          ],
                          "tokenizer": "my_standard"
                        },
                        "ngram_sc_ws_analyzer": {
                          "filter": [
                            "lowercase",
                            "ngram_filter"
                          ],
                          "char_filter": [
                            "remove_special_char_filter",
                            "remove_whitespace_filter"
                          ],
                          "tokenizer": "standard"
                        },
                        "auth_analyzer": {
                          "filter": [
                            "lowercase"
                          ],
                          "char_filter": [
                            "remove_filter"
                          ],
                          "tokenizer": "standard"
                        }
                      },
                      "tokenizer": {
                        "my_whitespace": {
                          "type": "whitespace",
                          "max_token_length": "30"
                        },
                        "my_keyword": {
                          "type": "keyword",
                          "max_token_length": "20"
                        },
                        "my_bigram": {
                          "token_chars": [
                            "letter",
                            "digit",
                            "punctuation"
                          ],
                          "max_token_length": "20",
                          "min_gram": "2",
                          "type": "ngram",
                          "max_gram": "20"
                        },
                        "my_standard": {
                          "type": "standard",
                          "max_token_length": "30"
                        },
                        "my_pattern": {
                          "pattern": "[\\\\^\\\\^]",
                          "type": "pattern"
                        },
                        "category_tokenizer": {
                          "pattern": "[\\\\/]",
                          "type": "pattern"
                        },
                        "edge_ngram": {
                          "token_chars": [
                            "letter",
                            "digit",
                            "punctuation"
                          ],
                          "min_gram": "1",
                          "type": "edge_ngram",
                          "max_gram": "20"
                        },
                        "my_ngram": {
                          "token_chars": [
                            "letter",
                            "digit",
                            "punctuation"
                          ],
                          "max_token_length": "20",
                          "min_gram": "1",
                          "type": "ngram",
                          "max_gram": "20"
                        }
                      }
                    }
                  }
                }
                """.formatted(dimension);
    }
}