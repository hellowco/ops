package kr.co.proten.llmops.api.app.service;

import kr.co.proten.llmops.api.app.dto.request.AppCreateDTO;
import kr.co.proten.llmops.api.app.dto.request.AppUpdateDTO;
import kr.co.proten.llmops.api.app.dto.response.AppResponseDTO;

import java.util.List;

public interface AppService {

    AppResponseDTO createApp(AppCreateDTO appCreateDTO);

    AppResponseDTO getAppById(String appId);

    List<AppResponseDTO> getAllApps(String workspaceId);

    AppResponseDTO updateApp(AppUpdateDTO appUpdateDTO);

    boolean deleteApp(String appId);
}
