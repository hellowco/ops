package kr.co.proten.llmops.api.user.mapper;

import kr.co.proten.llmops.api.user.dto.response.UserDTO;
import kr.co.proten.llmops.api.user.entity.User;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface UserMapper {
    UserDTO fromEntity(User user);
}