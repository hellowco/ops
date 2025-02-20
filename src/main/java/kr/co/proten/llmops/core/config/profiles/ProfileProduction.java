package kr.co.proten.llmops.core.config.profiles;

import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.context.annotation.PropertySource;

@Configuration
@Profile(value="prod")
@PropertySource({"classpath:config/prod/application.properties"})
public class ProfileProduction {

}
