package kr.co.proten.llmops.core.exception;

public class ChatProcessingException extends RuntimeException {
    public ChatProcessingException(String message) {
        super(message);
    }
}