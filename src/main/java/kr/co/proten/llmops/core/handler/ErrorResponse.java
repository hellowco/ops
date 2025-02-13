package kr.co.proten.llmops.core.handler;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Getter
@NoArgsConstructor
public class ErrorResponse {

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime timestamp;
    private String status;
    private String message;

    public ErrorResponse(LocalDateTime timestamp, String message) {
        this.timestamp = timestamp;
        this.status = "FAIL";
        this.message = message;
    }
}
