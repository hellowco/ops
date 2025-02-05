package kr.co.proten.llmops.api.workflow.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonSetter;
import com.fasterxml.jackson.annotation.Nulls;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class FlowNode {
    private String id;
    private NodeData data;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class NodeData {
        private String desc;
        private String type;
        private String title;
        private boolean selected;
        @JsonProperty("variables")
        private List<String> variables;
        @JsonSetter(nulls = Nulls.AS_EMPTY)
        private List<Dataset> datasets = new ArrayList<>();
        @JsonProperty("llm_settings")
        private LLMSetting llmSettings;

        @Data
        @NoArgsConstructor
        @AllArgsConstructor
        @JsonIgnoreProperties(ignoreUnknown = true)
        public static class Dataset {
            @JsonProperty("model_name")
            private String modelName;
            @JsonProperty("knowledge_name")
            private String knowledgeName;
            @JsonProperty("model_type")
            private String modelType;
            private String query;
            @JsonProperty("search_type")
            private String searchType;
            @JsonProperty("keyword_weight")
            private String keywordWeight;
            @JsonProperty("vector_weight")
            private String vectorWeight;
            private String k;
            private String page;
            @JsonProperty("page_size")
            private String pageSize;
        }

        @Data
        @NoArgsConstructor
        @AllArgsConstructor
        @JsonIgnoreProperties(ignoreUnknown = true)
        public static class LLMSetting {
            private LLMModel model;
            @JsonSetter(nulls = Nulls.AS_EMPTY)
            @JsonProperty("prompt_template")
            private List<Prompt> prompt = new ArrayList<>();
            private List<Object> context;
        }

        @Data
        @NoArgsConstructor
        @AllArgsConstructor
        @JsonIgnoreProperties(ignoreUnknown = true)
        public static class LLMModel {
            private String provider;
            private String name;
            private String mode;
            @JsonProperty("api_key")
            private String apiKey;
            @JsonProperty("completion_params")
            private Params completionParams;
        }

        @Data
        @NoArgsConstructor
        @AllArgsConstructor
        @JsonIgnoreProperties(ignoreUnknown = true)
        public static class Params {
            private String temperature;
            @JsonProperty("top_p")
            private String topP;
            @JsonProperty("top_k")
            private String topK;
            @JsonProperty("repeat_penalty")
            private String repeatPenalty;
            @JsonProperty("num_predict")
            private String numPredict;
            private String mirostat;
            @JsonProperty("mirostat_eta")
            private String mirostatEta;
            @JsonProperty("mirostat_tau")
            private String mirostatTau;
            @JsonProperty("num_ctx")
            private String numCtx;
        }

        @Data
        @NoArgsConstructor
        @AllArgsConstructor
        @JsonIgnoreProperties(ignoreUnknown = true)
        public static class Prompt {
            private String role;
            private String text;
            private String id;
        }

    }
}
