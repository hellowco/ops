package kr.co.proten.llmops.core.helpers;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.opensearch.client.json.JsonData;
import org.opensearch.client.opensearch._types.mapping.Property;
import org.opensearch.client.opensearch._types.mapping.TypeMapping;
import org.opensearch.client.opensearch.indices.IndexSettings;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

@Slf4j
public class MappingLoader {
    public static Map<String, Object> loadMappingFromResources(String resourcePath) throws IOException {
        Map<String, Object> result = new HashMap<>();

        // ClassLoader를 사용하여 resources 폴더에서 파일 읽기
        try (InputStream inputStream = MappingLoader.class.getClassLoader().getResourceAsStream(resourcePath)) {
            if (inputStream == null) {
                throw new IOException("Resource not found: " + resourcePath);
            }
            ObjectMapper objectMapper = new ObjectMapper();
            JsonNode rootNode = objectMapper.readTree(inputStream);

            // Settings 추출
            JsonNode settingsNode = rootNode.get("settings");
            log.info("Settings: {}", settingsNode);
            IndexSettings settings = objectMapper.treeToValue(settingsNode, IndexSettings.class);
//            log.info("Settings: {}", settings);
//            result.put("settings", settings);

            // Mappings 추출
            JsonNode mappingsNode = rootNode.get("mappings");
            log.info("Mappings: {}", mappingsNode);
//            TypeMapping mappings = objectMapper.treeToValue(mappingsNode, TypeMapping.class);
//            log.info("Mappings: {}", mappings);
//            result.put("mappings", mappings);

            log.info("result: {}",result);
        } catch (Exception e) {
            e.getMessage();
            throw new RuntimeException(e);
        }
        return result;
    }

    public static Map<String, Property> convertToProperties(Map<String, Object> rawMapping) {
        Map<String, Property> properties = new HashMap<>();

        rawMapping.forEach((key, value) -> {
            try {
                Map<String, Object> valueMap = (Map<String, Object>) value;
                String type = (String) valueMap.get("type");

                if ("date".equals(type)) {
                    properties.put(key, new Property.Builder().date(d -> d).build());
                } else if ("keyword".equals(type)) {
                    properties.put(key, new Property.Builder().keyword(k -> k.ignoreAbove(256)).build());
                } else if ("text".equals(type)) {
                    properties.put(key, new Property.Builder().text(t -> t).build());
                } else if ("boolean".equals(type)) {
                    properties.put(key, new Property.Builder().boolean_(b -> b).build());
                } else if ("long".equals(type)) {
                    properties.put(key, new Property.Builder().long_(l -> l).build());
                } else if ("knn_vector".equals(type)) {
                    // knn_vector-specific processing
                    @SuppressWarnings("unchecked")
                    Map<String, Object> methodConfig = (Map<String, Object>) valueMap.get("method");
                    String engine = (String) methodConfig.get("engine");
                    String spaceType = (String) methodConfig.get("space_type");
                    String name = (String) methodConfig.get("name");
                    @SuppressWarnings("unchecked")
                    Map<String, Object> parameters = (Map<String, Object>) methodConfig.get("parameters");
                    int efConstruction = (int) parameters.get("ef_construction");
                    int m = (int) parameters.get("m");

                    // parameters를 JsonData로 변환하여 Map 생성
                    Map<String, JsonData> parameterMap = new HashMap<>();
                    parameterMap.put("ef_construction", JsonData.of(efConstruction));
                    parameterMap.put("m", JsonData.of(m));
                    // Add knn_vector property
                    properties.put(key, new Property.Builder()
                            .knnVector(k -> k
                                    .dimension((Integer) valueMap.get("dimension"))
                                    .method(mb -> mb
                                            .engine(engine)
                                            .spaceType(spaceType)
                                            .name(name)
                                            .parameters(parameterMap)
                                    )
                            )
                            .build());
                } else {
                    throw new IllegalArgumentException("Unsupported type: " + type);
                }
            } catch (Exception e) {
                throw new RuntimeException("Failed to convert mapping for key: " + key, e);
            }
        });

        return properties;
    }

    /*
    public static IndexSettings convertToIndexSettings(Map<String, Object> settingsMap) {
        return new IndexSettings.Builder()
                .numberOfShards("5")
                .numberOfReplicas("1")
                .analysis(
                        new IndexSettingsAnalysis.Builder()
                                .analyzer("edge_analyzer", analyzer -> analyzer.custom(
                                        new CustomAnalyzer.Builder().filter("lowercase")
                                                .tokenizer("edge_ngram")
                                                .build()))
                                .analyzer("ngram_analyzer", analyzer -> analyzer.custom(
                                        new CustomAnalyzer.Builder().filter(List.of("lowercase","ngram_filter"))
                                                .tokenizer("my_whitespace")
                                                .build()))
                                .analyzer("category_analyzer", analyzer -> analyzer.custom(
                                        new CustomAnalyzer.Builder()
                                                .tokenizer("category_tokenizer")
                                                .build()))
                                .analyzer("reverse_ngram_analyzer", analyzer -> analyzer.custom(
                                        new CustomAnalyzer.Builder().filter(List.of("lowercase","reverse","edge_filter","reverse"))
                                                .tokenizer("standard")
                                                .build()))
                                .analyzer("reverse_ngram_sc_ws_analyzer", analyzer -> analyzer.custom(
                                        new CustomAnalyzer.Builder().filter(List.of("lowercase","reverse","edge_filter","reverse"))
                                                .charFilter(List.of("remove_special_char_filter","remove_whitespace_filter"))
                                                .tokenizer("standard")
                                                .build()))
                                .build()
                )
//                .put("max_ngram_diff", "20")
//                .put("max_inner_result_window", "1000")
//                .put("max_result_window", "1000000")
//                .put("knn", true)
//                .similarity("default", similarity -> similarity.bm25(bm25 -> bm25.b(0.0).k1(1.2)))
//                .blocks(b -> b.readOnlyAllowDelete(null)) // blocks 값
                .build();

    }
    */

    public static Map<String, Object> convertToMap(Object obj) {
        ObjectMapper objectMapper = new ObjectMapper();
        return objectMapper.convertValue(obj, Map.class);
    }
}