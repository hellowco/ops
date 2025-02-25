package kr.co.proten.llmops.api.model.service.factory;

import kr.co.proten.llmops.api.model.service.ModelService;
import org.springframework.stereotype.Component;

import java.util.*;

@Component
public class ProviderFactory {
    private final Map<String, ModelService> modelSerivceMap = new HashMap<>();

    public ProviderFactory(List<ModelService> modelServices) {
        modelServices.forEach(s -> modelSerivceMap.put(s.getProviderType(), s));
    }

    public Optional<ModelService> getProvider(String providerType) {
        return Optional.ofNullable(modelSerivceMap.get(providerType));
    }

    public Collection<ModelService> getAllServices() {
        return modelSerivceMap.values();
    }
}