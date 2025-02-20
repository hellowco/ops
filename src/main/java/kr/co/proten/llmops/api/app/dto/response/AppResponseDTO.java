package kr.co.proten.llmops.api.app.dto.response;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@ToString
@Data
@NoArgsConstructor
@AllArgsConstructor
public class AppResponseDTO {

    @Schema(description = "워크스페이스 ID", example = "8ee589ef-c7bb-4f2a-a773-630abd0de8c7")
    private String appId;

    @Schema(description = "워크스페이스 이름", example = "다락방")
    private String name;

    @Schema(description = "워크스페이스 설명", example = "프로텐 E-Sports 동아리")
    private String description;

    @Schema(description = "워크플로우 ID", example = "8ea529ef-17bc-4ffa-a7c3-230bbd0de8c7")
    private String workflowId;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
    @Schema(description = "워크스페이스 생성날짜", example = "yyyy-MM-dd HH:mm:ss")
    private String createdAt;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
    @Schema(description = "워크스페이스 수정날짜", example = "yyyy-MM-dd HH:mm:ss")
    private String updatedAt;

    @Schema(description = "워크스페이스 활성상태", example = "8ee589ef-c7bb-4f2a-a773-630abd0de8c7")
    private boolean isActive;
}
