package kr.co.proten.llmops.api.user.serivce;

import kr.co.proten.llmops.api.user.dto.request.SignupDTO;
import kr.co.proten.llmops.api.user.dto.request.UserLoginDTO;
import kr.co.proten.llmops.api.user.dto.request.UserUpdateDTO;
import kr.co.proten.llmops.api.user.dto.response.AuthResponseDto;
import kr.co.proten.llmops.api.user.dto.response.UserDTO;
import kr.co.proten.llmops.api.user.entity.User;

import java.util.List;

public interface UserService {
    UserDTO createUser(SignupDTO dto);

    AuthResponseDto login(UserLoginDTO requestDto);

    void logout(String token);

    UserDTO updateUser(String userId, UserUpdateDTO dto);

    void deleteUser(String userId);

    UserDTO getUser(String userId);

    List<UserDTO> getAllUsers(int page, int size, String sortField, String sortBy);

    List<String> getUserWorkspaces(String token);

    AuthResponseDto selectWorkspace(String token, String workspaceId);
}
