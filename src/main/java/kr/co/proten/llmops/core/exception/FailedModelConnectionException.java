package kr.co.proten.llmops.core.exception;

public class FailedModelConnectionException extends RuntimeException {
    public FailedModelConnectionException(String message) {
        super(message);
    }
}
