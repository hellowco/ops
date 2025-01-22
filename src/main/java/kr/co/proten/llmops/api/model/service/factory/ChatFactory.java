package kr.co.proten.llmops.api.model.service.factory;

import kr.co.proten.llmops.api.model.service.ChatService;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Component
public class ChatFactory {
    private final Map<String, ChatService> chatSerivceMap = new HashMap<>();

    public ChatFactory(List<ChatService> chatServices) {
        chatServices.forEach(s -> chatSerivceMap.put(s.getServiceType(), s));
    }

    public Optional<ChatService> getChatService(String chunkType) {
        return Optional.ofNullable(chatSerivceMap.get(chunkType));
    }
}