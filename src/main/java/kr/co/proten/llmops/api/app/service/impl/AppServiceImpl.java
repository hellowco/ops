package kr.co.proten.llmops.api.app.service.impl;

import kr.co.proten.llmops.api.app.entity.AppEntity;
import kr.co.proten.llmops.api.app.repository.AppRepository;
import kr.co.proten.llmops.api.app.service.AppService;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class AppServiceImpl implements AppService {

    private final AppRepository appRepository;

    public AppServiceImpl(AppRepository appRepository) {
        this.appRepository = appRepository;
    }

    @Override
    public AppEntity createApp(AppEntity appEntity) {
        return appRepository.save(appEntity);
    }

    @Override
    public AppEntity getAppById(String id) {
        return appRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("App not found with id: " + id));
    }

    @Override
    public List<AppEntity> getAllApps() {
        return appRepository.findAll();
    }

    @Override
    public AppEntity updateApp(String id, AppEntity appEntity) {
        AppEntity existingApp = getAppById(id);
//        existingApp.setName(appEntity.getName());
//        existingApp.setDescription(appEntity.getDescription());
//        existingApp.setUpdatedAt(appEntity.getUpdatedAt());
//        existingApp.setActive(appEntity.isActive());
        return appRepository.save(existingApp);
    }

    @Override
    public void deleteApp(String id) {
        appRepository.deleteById(id);
    }
}
