package kr.co.proten.llmops;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@SpringBootApplication
@EnableAspectJAutoProxy
@EnableJpaAuditing
//@SpringBootApplication(exclude={DataSourceAutoConfiguration.class})
public class LLMopsApiApplication {
    public static void main(String[] args) {
        SpringApplication.run(LLMopsApiApplication.class, args);
    }
}
