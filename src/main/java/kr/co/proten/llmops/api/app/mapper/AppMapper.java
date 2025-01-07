package kr.co.proten.llmops.api.app.mapper;

import kr.co.proten.llmops.api.app.dto.request.AppCreateDTO;
import kr.co.proten.llmops.api.app.dto.request.AppUpdateDTO;
import kr.co.proten.llmops.api.app.dto.response.AppResponseDTO;
import kr.co.proten.llmops.api.app.entity.AppEntity;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface AppMapper {
    AppResponseDTO responseToDto(AppEntity entity);
    AppEntity createToEntity(AppCreateDTO dto);
    AppEntity updateToEntity(AppUpdateDTO dto);
}
