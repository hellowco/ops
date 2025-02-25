package kr.co.proten.llmops.api.model.dto.response;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import kr.co.proten.llmops.api.model.entity.Model;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.format.DateTimeFormatter;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ModelResponseDTO {

    @Schema(description = "모델 ID")
    String modelId;

    @Schema(description = "모델 이름")
    String name;

    @Schema(description = "모델 제공자")
    String provider;

    @Schema(description = "모델 타입(embed/search)")
    String modelType;
    
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
    @Schema(description = "모델 등록날짜", example = "yyyy-MM-dd HH:mm:ss")
    String createdAt;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
    @Schema(description = "모델 수정날짜", example = "yyyy-MM-dd HH:mm:ss")
    String updatedAt;

    public static ModelResponseDTO entityToResponseDTO(Model model) {
        return ModelResponseDTO.builder()
                .modelId(model.getModelId())
                .name(model.getName())
                .provider(model.getProvider().getName())
                .modelType(model.getType().getType())
                .createdAt( DateTimeFormatter.ISO_LOCAL_DATE_TIME.format( model.getCreatedAt() ))
                .updatedAt( DateTimeFormatter.ISO_LOCAL_DATE_TIME.format( model.getUpdatedAt() ))
                .build();
    }
}
