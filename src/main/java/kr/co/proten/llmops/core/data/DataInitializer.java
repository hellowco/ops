package kr.co.proten.llmops.core.data;

import jakarta.annotation.PostConstruct;
import kr.co.proten.llmops.api.user.entity.User;
import kr.co.proten.llmops.api.user.repository.UserRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.beans.factory.annotation.Autowired;

import static kr.co.proten.llmops.core.helpers.UUIDGenerator.generateUUID;

@Slf4j
@Component
public class DataInitializer {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Autowired
    public DataInitializer(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @PostConstruct
    public void init() {
        String defaultId = "PROADMIN";
        String defaultPassword = "proten1!";

        if (userRepository.count() == 0) {
            User defaultUser =
                    User.builder()
                            .userId(defaultId)
                            .username(defaultId)
                            .email("proadmin@proten.co.kr")
                            .password(passwordEncoder.encode(defaultPassword))
                            .role("ADMIN")
                            .build();
            
            userRepository.save(defaultUser);

            log.info("유저 기본 데이터: \t\n id: {}, password: {} \n @PostConstruct 에서 추가되었습니다.", defaultId, defaultPassword);
        }
    }
}
