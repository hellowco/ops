package kr.co.proten.llmops.core.handler;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
@RequiredArgsConstructor
public class CustomAccessDeniedHandler implements AccessDeniedHandler {

    private final GlobalExceptionHandler globalExceptionHandler;
    private final ObjectMapper objectMapper;

    @Override
    public void handle(HttpServletRequest request, HttpServletResponse response,
                       AccessDeniedException accessDeniedException) throws IOException, ServletException {
        ResponseEntity<Object> errorResponse = globalExceptionHandler.buildErrorResponse(
                HttpStatus.FORBIDDEN, "Not Available to Access");

        // 요청의 Accept 헤더를 확인
        String acceptHeader = request.getHeader("Accept");
        if (acceptHeader != null && acceptHeader.contains(MediaType.TEXT_EVENT_STREAM_VALUE)) {
            // 클라이언트가 SSE를 기대하는 경우
            response.setContentType(MediaType.TEXT_EVENT_STREAM_VALUE);
        } else {
            // 그 외의 경우 JSON으로 응답
            response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        }

        response.setStatus(HttpServletResponse.SC_FORBIDDEN);
        response.getWriter().write(objectMapper.writeValueAsString(errorResponse.getBody()));
    }
}
