package kr.co.proten.llmops.core.aop;

import kr.co.proten.llmops.core.config.OpenSearchConfig;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.opensearch.client.opensearch.OpenSearchClient;

@Aspect
@Component
public class OpenSearchConnectAspect {

    private final Logger log = LoggerFactory.getLogger(this.getClass());

    @Value("${search.servers}")
    private String[] searchServers;

    @Value("${search.username}")
    private String userName;

    @Value("${search.password}")
    private String password;

    private static final ThreadLocal<OpenSearchClient> threadLocalClient = new ThreadLocal<>();

    @Around("execution(* kr.co.proten.llmops.api..repository..*Repository.*(..))") // 각 도메인의 repo 디렉토리 및 하위 디렉토리
    public Object manageConnection(ProceedingJoinPoint joinPoint) throws Throwable {
        OpenSearchClient client = null;

        try {
            // OpenSearch 연결 생성
            client = OpenSearchConfig.createConnection(searchServers, userName, password);
            threadLocalClient.set(client);
            log.info("OpenSearch connection established for {}", joinPoint.getSignature().getName());

            // 대상 메서드 실행
            return joinPoint.proceed();
        } catch (Exception e) {
            log.error("Error occurred during OpenSearch operation in {}: {}",
                    joinPoint.getSignature(), e.getMessage(), e);
            throw e; // 예외를 다시 던져 상위 호출자에게 알림
        } finally {
            // OpenSearch 연결 해제
            OpenSearchConfig.closeConnection(client);
            threadLocalClient.remove();
            log.info("OpenSearch connection closed for {}", joinPoint.getSignature().getName());
        }
    }

    public static OpenSearchClient getClient() {
        return threadLocalClient.get();
    }
}
