package kr.co.proten.llmops.core.data;

import jakarta.annotation.PostConstruct;
import kr.co.proten.llmops.api.model.entity.ModelType;
import kr.co.proten.llmops.api.model.entity.Provider;
import kr.co.proten.llmops.api.model.repository.ModelTypeRepository;
import kr.co.proten.llmops.api.model.repository.ProviderRepository;
import kr.co.proten.llmops.api.user.entity.User;
import kr.co.proten.llmops.api.user.repository.UserRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static kr.co.proten.llmops.core.helpers.UUIDGenerator.generateUUID;

@Slf4j
@Component
public class DataInitializer {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final ProviderRepository providerRepository;
    private final ModelTypeRepository modelTypeRepository;

    @Value("${app.icon.base-path}")
    private String basePath;

    @Autowired
    public DataInitializer(UserRepository userRepository, PasswordEncoder passwordEncoder, ProviderRepository providerRepository, ModelTypeRepository modelTypeRepository) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.providerRepository = providerRepository;
        this.modelTypeRepository = modelTypeRepository;
    }

    @PostConstruct
    public void init() {
        makeAdminAccount();
        makeDefaultProvider();
        makeDefaultModelType();
    }

    private void makeAdminAccount() {
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

            log.info("유저 기본 데이터: \t\n id: {}, password: {} \n DataInitializer 에서 추가되었습니다.", defaultId, defaultPassword);
        }
    } // end of makeAdminAccount

    private void makeDefaultProvider() {
        // ProviderData 리스트 생성
        List<ProviderData> providerDataList = List.of(
                new ProviderData("DEFAULT", "Provided by PROTEN.", "vsearch-logo.svg"),
                new ProviderData("OLLAMA", "Models provided by Ollama, after added to Ollama service.", "ollama-logo.png"),
                new ProviderData("OPENAI", "Models provided by OpenAI, such as GPT-3.5-Turbo and GPT-4.", "openai-logo.png")
        );

        if (providerRepository.count() == 0) {
            // Provider 엔티티로 변환 후 saveAll 사용
            List<Provider> providers = providerDataList.stream()
                    .map(data -> Provider.builder()
                            .name(data.getName())
                            .description(data.getDescription())
                            .icon(basePath + data.getIcon())
                            .build())
                    .toList();

            providerRepository.saveAll(providers);
            providers.forEach(provider ->
                    log.info("모델 제공자 기본 데이터: \n provider: {} \n DataInitializer 에서 추가되었습니다.", provider.getName())
            );
        }
    } // end of makeDefaultProvider

    private void makeDefaultModelType() {
        List<String> types = List.of("SEARCH", "EMBED");

        if (modelTypeRepository.count() == 0) {
            for (String type : types) {
                ModelType modelType = ModelType.builder()
                        .type(type)
                        .build();

                modelTypeRepository.save(modelType);
                log.info("모델 타입 기본 데이터: \t\n type: {} \n DataInitializer 에서 추가되었습니다.", type);
            } // end of for
        } // end of if
    } // end of makeDefaultModelType

}
