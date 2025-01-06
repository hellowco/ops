package kr.co.proten.llmops.core.advice;

import groovy.util.logging.Log;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.reactive.result.method.annotation.ResponseEntityExceptionHandler;

import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice(basePackages = {
        "kr.co.proten.llmops.api.app.controller"
})
public class ExceptionRestControllerAdvice extends ResponseEntityExceptionHandler {

    private static final String FAIL = "fail";
    private final Logger log = LoggerFactory.getLogger(this.getClass());

    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<Map<String, Object>> handleRuntimeException(RuntimeException ex) {
        Map<String, Object> response = new HashMap<>();
        response.put("status", FAIL);
        response.put("message", ex.getMessage());
        return ResponseEntity.status(500).body(response);
    }
}
