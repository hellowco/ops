package kr.co.proten.llmops.core.aop;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import jakarta.servlet.http.HttpServletRequest;

@Aspect
@Component
public class LoggingAspect {

    private final Logger log = LoggerFactory.getLogger(this.getClass());
    private final HttpServletRequest request;

    public LoggingAspect(HttpServletRequest request) {
        this.request = request;
    }

    // Pointcut 정의: 모든 @RestController 메서드
    @Pointcut("within(@org.springframework.web.bind.annotation.RestController *)")
    public void restControllerMethods() {}

    // Before Advice: 메서드 실행 전 실행
    @Before("restControllerMethods()")
    public void logControllerMethod(JoinPoint joinPoint) {
        // 요청 URL 가져오기
        String requestUrl = request.getRequestURI();

        // 호출된 메서드 이름
        String methodName = joinPoint.getSignature().getName();

        log.info("Method called: {}, URL: {}", methodName, requestUrl);
    }
}
