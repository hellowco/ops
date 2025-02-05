package kr.co.proten.llmops.core.exception;

public class WorkflowExecutionException extends RuntimeException {
    public WorkflowExecutionException(String message) {
        super(message);
    }
}