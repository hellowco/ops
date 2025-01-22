package kr.co.proten.llmops.api.model.service.impl;

import kr.co.proten.llmops.api.model.dto.request.ModelRequest;
import kr.co.proten.llmops.core.config.ai.OllamaConfig;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class OllamaChatService extends AbstractChatService {

    private static final Logger log = LoggerFactory.getLogger(OllamaChatService.class);
    private final OllamaConfig ollamaConfig;

    @Override
    public String getServiceType() {
        return "ollama";
    }

    @Override
    protected ChatModel createChatModel(ModelRequest request) {
        final String host = "192.168.0.28";
        final int port = 11434;
        return ollamaConfig.createChatModel(host, port, request.model());
    }
}