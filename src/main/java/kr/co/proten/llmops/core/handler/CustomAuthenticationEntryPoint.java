package kr.co.proten.llmops.core.handler;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
@RequiredArgsConstructor
public class CustomAuthenticationEntryPoint implements AuthenticationEntryPoint {

    private final GlobalExceptionHandler globalExceptionHandler;
    private final ObjectMapper objectMapper;

    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response,
                         AuthenticationException authException) throws IOException, ServletException {
        ResponseEntity<Object> errorResponse = globalExceptionHandler.buildErrorResponse(
                HttpStatus.UNAUTHORIZED, "Require an Authorization");

        // 요청의 Accept 헤더를 확인
        String acceptHeader = request.getHeader("Accept");
        if (acceptHeader != null && acceptHeader.contains(MediaType.TEXT_EVENT_STREAM_VALUE)) {
            // 클라이언트가 SSE를 기대하는 경우
            response.setContentType(MediaType.TEXT_EVENT_STREAM_VALUE);
        } else {
            // 그 외의 경우 JSON으로 응답
            response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        }

        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        response.getWriter().write(objectMapper.writeValueAsString(errorResponse.getBody()));
    }
}
