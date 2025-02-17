package kr.co.proten.llmops.core.exception;

public class WorkspaceAlreadyExistException extends RuntimeException {
    public WorkspaceAlreadyExistException(String message) {
        super(message);
    }
}
