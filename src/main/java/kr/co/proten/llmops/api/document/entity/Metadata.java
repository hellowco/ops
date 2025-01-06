package kr.co.proten.llmops.api.document.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

import java.time.LocalDateTime;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
@JsonIgnoreProperties(ignoreUnknown = true)
public class Metadata {
    @JsonProperty("id")
    private String id;

    @JsonProperty("docId")
    private String docId;

    @JsonProperty("index")
    private String index;

    @JsonProperty("description")
    private String description;

    @JsonProperty("isActive")
    private boolean isActive;

    @JsonProperty("lastUpdatedDate")
    private LocalDateTime lastUpdatedDate;

    @JsonProperty("convertDate")
    private LocalDateTime convertDate;

    @JsonProperty("orgFileName")
    private String orgFileName;

    @JsonProperty("orgFilePath")
    private String orgFilePath;

    @JsonProperty("totalPage")
    private long totalPage;

    @JsonProperty("chunkSize")
    private long chunkSize;

    @JsonProperty("chunkNum")
    private long chunkNum;

    @JsonProperty("pdfFileName")
    private String pdfFileName;

    @JsonProperty("pdfFilePath")
    private String pdfFilePath;

    @JsonProperty("userId")
    private String userId;

    @JsonProperty("version")
    private String version;
}
