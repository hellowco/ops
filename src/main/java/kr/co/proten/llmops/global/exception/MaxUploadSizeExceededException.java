package kr.co.proten.llmops.global.exception;

public class MaxUploadSizeExceededException extends RuntimeException {
    public MaxUploadSizeExceededException(String message) {
        super(message);
    }

    public MaxUploadSizeExceededException(String message, Throwable cause) {
        super(message, cause);
    }
}
