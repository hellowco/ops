package kr.co.proten.llmops.core.exception;

public class StateChangeException extends RuntimeException {
    public StateChangeException(String message) {
        super(message);
    }
}
