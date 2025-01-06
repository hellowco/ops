package kr.co.proten.llmops.api.app.service;

import kr.co.proten.llmops.api.app.entity.AppEntity;

import java.util.List;

public interface AppService {
    AppEntity createApp(AppEntity appEntity);
    AppEntity getAppById(String id);
    List<AppEntity> getAllApps();
    AppEntity updateApp(String id, AppEntity appEntity);
    void deleteApp(String id);
}
