package kr.co.proten.llmops.api.workspace.dto.request;

import io.swagger.v3.oas.annotations.Hidden;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import kr.co.proten.llmops.core.exception.InvalidInputException;
import kr.co.proten.llmops.core.validation.ValidNumber;
import org.springframework.util.Assert;

public record WorkspaceCreateDTO(

        @NotBlank
        @Schema(description = "워크스페이스 이름", example = "다락방")
        String name,

        @NotBlank
        @Schema(description = "설명", example = "프로텐 e-sports 동아리")
        String description,

        @NotBlank
        @ValidNumber(type = ValidNumber.NumberType.INT, message = "tokenLimit must be a valid integer")
        String tokenLimit,
        
        @NotBlank
        @Schema(description = "소유주 id", example = "PROADMIN")
        String workspaceOwner
) {

    public void validate() {
        // Validate k
        if (tokenLimit != null) {
            int tokenValue = parseIntOrThrow(tokenLimit, "tokenLimit must be a valid integer");
            Assert.isTrue(tokenValue > 0, "tokenLimit value must be greater than 0");
        }
    }

    @Hidden
    public int getTokenAsInt() {
        return parseIntOrThrow(tokenLimit, "tokenLimit must be a valid integer");
    }

    private int parseIntOrThrow(String value, String errorMessage) {
        try {
            return Integer.parseInt(value);
        } catch (NumberFormatException e) {
            throw new InvalidInputException(errorMessage);
        }
    }
}
