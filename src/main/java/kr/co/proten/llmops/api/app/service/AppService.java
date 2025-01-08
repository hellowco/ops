package kr.co.proten.llmops.api.app.service;

import kr.co.proten.llmops.api.app.dto.request.AppCreateDTO;
import kr.co.proten.llmops.api.app.dto.request.AppSearchDTO;
import kr.co.proten.llmops.api.app.dto.request.AppStateDTO;
import kr.co.proten.llmops.api.app.dto.request.AppUpdateDTO;
import kr.co.proten.llmops.api.app.dto.response.AppResponseDTO;

import java.util.List;

public interface AppService {

    AppResponseDTO createApp(AppCreateDTO appCreateDTO);

    AppResponseDTO getAppById(String workspaceId, String appId);

    List<AppResponseDTO> getAppByName(AppSearchDTO appSearchDTO);

    List<AppResponseDTO> getAllApps(String workspaceId, int page, int size, String sortField, String sortBy);

    AppResponseDTO updateApp(AppUpdateDTO appUpdateDTO);

    AppResponseDTO updateAppState(AppStateDTO appStateDTO);

    boolean deleteApp(String appId);

    boolean deleteAppList(List<String> appIdList);
}
