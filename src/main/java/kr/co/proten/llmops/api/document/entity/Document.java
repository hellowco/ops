package kr.co.proten.llmops.api.document.entity;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import javax.annotation.Nullable;
import java.util.List;

@Getter
@Setter
@Builder
public class Document {
    @JsonProperty("id")
    String id;

    @JsonProperty("docId")
    String docId;

    @JsonProperty("index")
    String index;

    @JsonProperty("isActive")
    boolean isActive;

    @JsonProperty("content")
    String content;

    @JsonProperty("content_vec")
    @Nullable
    List<Double> contentVec;

    @JsonProperty("page")
    long page;
}
