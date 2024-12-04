package kr.co.proten.llmops.global.exception;

public class UnsupportedFileExtensionException extends RuntimeException {
    public UnsupportedFileExtensionException(String message) {
        super(message);
    }
}
