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

import java.util.List;

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

    private static final String BASE_PACKAGE = "kr.co.proten.llmops.api.";

    private final List<String> opensearchDomains;

    private static final ThreadLocal<OpenSearchClient> threadLocalClient = new ThreadLocal<>();

    public OpenSearchConnectAspect(@Value("${opensearch-domains}") List<String> opensearchDomains) {
        this.opensearchDomains = opensearchDomains;
    }

    @Around("execution(* kr.co.proten.llmops.api..repository..*Repository.*(..))")
    public Object manageConnection(ProceedingJoinPoint joinPoint) throws Throwable {
        // 호출된 클래스의 패키지명
        String targetPackage = joinPoint.getTarget().getClass().getPackage().getName();

        if(isAllowedDomain(targetPackage)){
            log.info("OpenSearch domain found for target package: {}", targetPackage);
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
        return joinPoint.proceed();
    }

    public static OpenSearchClient getClient() {
        return threadLocalClient.get();
    }

    private boolean isAllowedDomain(String packageName) {
        // 공통 패키지 + 허용된 도메인으로 전체 경로 생성 및 비교
        return opensearchDomains.stream()
                .map(domain -> BASE_PACKAGE + domain)
                .anyMatch(packageName::startsWith);
    }
}
