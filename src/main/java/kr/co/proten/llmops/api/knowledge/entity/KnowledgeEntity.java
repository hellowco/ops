package kr.co.proten.llmops.api.knowledge.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

@NoArgsConstructor
@AllArgsConstructor
@Builder
@Getter
@Setter
@JsonIgnoreProperties(ignoreUnknown = true)
public class KnowledgeEntity {
    @JsonProperty("id")
    private String id;

    @JsonProperty("modelName")
    private String modelName;

    @JsonProperty("knowledgeName")
    private String knowledgeName;

    @JsonProperty("description")
    private String description;
}