package kr.co.proten.llmops.api.document.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

import javax.annotation.Nullable;
import java.util.List;

@NoArgsConstructor
@AllArgsConstructor
@Builder
@Getter
@Setter
@JsonIgnoreProperties(ignoreUnknown = true)
public class Document {
    @JsonProperty("id")
    private String id;

    @JsonProperty("docId")
    private String docId;

    @JsonProperty("chunkId")
    private long chunkId;

    @JsonProperty("index")
    private String index;

    @JsonProperty("isActive")
    private boolean isActive;

    @JsonProperty("content")
    private String content;

    @JsonProperty("content_vec")
    @Nullable
    private List<Double> contentVec;

    @JsonProperty("page")
    @Nullable
    private Long page = 0L;

    @JsonProperty("@timestamp")
    @Nullable
    private String timestamp;

    @JsonProperty("_score")
    private double score;
}
