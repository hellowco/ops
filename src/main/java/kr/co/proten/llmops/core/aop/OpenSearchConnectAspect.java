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

    @Around("execution(* kr.co.proten.llmops.api..repository..*Repository.*(..))")
    public Object manageConnection(ProceedingJoinPoint joinPoint) throws Throwable {
        OpenSearchClient client = null;

        try {
            client = OpenSearchConfig.createConnection(searchServers, userName, password);
            threadLocalClient.set(client);
            log.info("OpenSearch connection established for {}", joinPoint.getSignature().getName());

            return joinPoint.proceed(); // 실제 메서드 실행
        } catch (Exception e) {
            log.error("Error during OpenSearch operation in {}", joinPoint.getSignature(), e);
            throw e;
        } finally {
            OpenSearchConfig.closeConnection(client);
            threadLocalClient.remove();
            log.info("OpenSearch connection closed for {}", joinPoint.getSignature().getName());
        }
    }

    public static OpenSearchClient getClient() {
        return threadLocalClient.get();
    }
}
