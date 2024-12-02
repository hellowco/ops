package kr.co.proten.llmops.api;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;

//@SpringBootApplication
@SpringBootApplication(exclude={DataSourceAutoConfiguration.class})
public class LlmopsApiApplication {

    public static void main(String[] args) {
        SpringApplication.run(LlmopsApiApplication.class, args);
    }

}
