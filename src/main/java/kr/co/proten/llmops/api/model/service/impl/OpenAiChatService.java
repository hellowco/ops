package kr.co.proten.llmops.api.model.service.impl;

import kr.co.proten.llmops.api.model.dto.request.ModelRequest;
import kr.co.proten.llmops.core.config.ai.OpenAiConfig;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class OpenAiChatService extends AbstractChatService {

    private final OpenAiConfig openAiConfig;

    @Value("${spring.ai.openai.api-key}")
    private String apiKey;

    @Override
    public String getServiceType() {
        return "openai"; // 제공자 이름
    }

    @Override
    protected ChatModel createChatModel(ModelRequest request) {
        apiKey = request.apiKey() == null ? apiKey : request.apiKey();
        return openAiConfig.createChatModel(apiKey, request.model());
    }
}
