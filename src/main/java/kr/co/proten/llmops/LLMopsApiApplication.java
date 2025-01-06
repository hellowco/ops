package kr.co.proten.llmops;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.context.annotation.EnableAspectJAutoProxy;

@SpringBootApplication
@EnableAspectJAutoProxy
//@SpringBootApplication(exclude={DataSourceAutoConfiguration.class})
public class LLMopsApiApplication {
    public static void main(String[] args) {
        SpringApplication.run(LLMopsApiApplication.class, args);
    }
}
