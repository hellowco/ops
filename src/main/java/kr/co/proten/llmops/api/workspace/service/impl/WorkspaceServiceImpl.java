package kr.co.proten.llmops.api.workspace.service.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import kr.co.proten.llmops.api.user.entity.User;
import kr.co.proten.llmops.api.user.entity.UserWorkspace;
import kr.co.proten.llmops.api.user.repository.UserRepository;
import kr.co.proten.llmops.api.user.repository.UserWorkspaceRepository;
import kr.co.proten.llmops.api.workspace.dto.WorkspaceCreateDTOValidator;
import kr.co.proten.llmops.api.workspace.dto.request.CustomPatchOperation;
import kr.co.proten.llmops.api.workspace.dto.request.UserPatchValue;
import kr.co.proten.llmops.api.workspace.dto.request.WorkspaceRequestDTO;
import kr.co.proten.llmops.api.workspace.dto.response.UserRoleDTO;
import kr.co.proten.llmops.api.workspace.dto.response.WorkspaceResponseDTO;
import kr.co.proten.llmops.api.workspace.entity.Workspace;
import kr.co.proten.llmops.api.workspace.mapper.WorkspaceMapper;
import kr.co.proten.llmops.api.workspace.repository.WorkspaceRepository;
import kr.co.proten.llmops.api.workspace.service.WorkspaceService;
import kr.co.proten.llmops.core.exception.ResourceNotFoundException;
import kr.co.proten.llmops.core.exception.WorkspaceAlreadyExistException;
import kr.co.proten.llmops.core.exception.WorkspaceCreationException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Slf4j
@RequiredArgsConstructor
@Service
@Transactional
public class WorkspaceServiceImpl implements WorkspaceService {

    private static final String SUCCESS = "success";

    private final ObjectMapper objectMapper;
    private final WorkspaceRepository workspaceRepository;
    private final WorkspaceMapper workspaceMapper;
    private final UserWorkspaceRepository userWorkspaceRepository;
    private final UserRepository userRepository;
    private final WorkspaceCreateDTOValidator validator;

    @Override
    @Transactional(readOnly = true)
    public Optional<Workspace> findWorkspaceById(String id){
        return workspaceRepository.findById(id);
    }

    @Override
    @Transactional
    public Map<String, Object> saveWorkspace(WorkspaceRequestDTO workspaceDTO) {
        Map<String, Object> resultMap = new HashMap<>();

        if (workspaceRepository.findByName(workspaceDTO.name()).isPresent()) {
            throw new WorkspaceAlreadyExistException("이미 존재하는 워크스페이스 이름입니다.");
        }

        validator.validate(workspaceDTO);

        try{
            Workspace workspace = workspaceMapper.requestToEntity(workspaceDTO);
            Workspace savedWS = workspaceRepository.save(workspace);

            List<UserWorkspace> userWorkspaceList = workspaceDTO.users().stream()
                    .map(userInfo -> {
                        // userInfo.userId()로 실제 User 엔티티 조회
                        User user = userRepository.findById(userInfo.userId())
                                .orElseThrow(() -> new ResourceNotFoundException("해당 ID의 사용자를 찾을 수 없습니다. ID: " + userInfo.userId()));
                        return UserWorkspace.builder()
                                .user(user)
                                .workspace(savedWS)
                                .role(userInfo.role())
                                .build();
                    })
                    .toList();

            userWorkspaceRepository.saveAll(userWorkspaceList);

            resultMap.put("status", SUCCESS);
            resultMap.put("msg", "회원가입 성공!");
            resultMap.put("response", getWorkspaceById(savedWS.getWorkspaceId()));

            return resultMap;
        } catch (Exception e) {
            throw new WorkspaceCreationException("워크스페이스 생성 중 오류가 발생했습니다.");
        }
    }

    @Override
    @Transactional(readOnly = true)
    public Map<String, Object> getWorkspaceById(String id) {
        Map<String, Object> resultMap = new HashMap<>();

        Workspace workspace = workspaceRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("해당 ID의 워크스페이스를 찾을 수 없습니다."));

        WorkspaceResponseDTO workspaceResponseDTO = workspaceMapper.entityToResponse(workspace);
        log.info("getWorkspaceById: {}", workspaceResponseDTO.toString());

        List<UserWorkspace> userWorkspaceList = userWorkspaceRepository.findUserByWorkspaceId(id);

        List<UserRoleDTO> userDTOList = userWorkspaceList.stream()
                .map(uw -> UserRoleDTO.builder()
                        .userId(uw.getUser().getUserId())
                        .username(uw.getUser().getUsername())
                        .email(uw.getUser().getEmail())
                        .role(uw.getRole())
                        .build())
                .collect(Collectors.toList());

        workspaceResponseDTO.setUsers(userDTOList);

        log.info("getWorkspaceById: {}", workspaceResponseDTO);

        resultMap.put("status", SUCCESS);
        resultMap.put("msg", "워크스페이스 반환 성공!");
        resultMap.put("response", workspaceResponseDTO);

        return resultMap;
    }

    @Override
    @Transactional(readOnly = true)
    public Map<String, Object> getAllWorkspaces(int page, int size, String sortField, String sortBy) {
        Map<String, Object> resultMap = new HashMap<>();

        page = page < 1 ? 0 : page - 1;

        Pageable pageable = sortBy.equalsIgnoreCase("ASC")
                ? PageRequest.of(page, size, Sort.by(Sort.Order.asc(sortField)))
                : PageRequest.of(page, size, Sort.by(Sort.Order.desc(sortField)));

        try{
            List<WorkspaceResponseDTO> responseDTOList =
                    workspaceRepository.findAll(pageable).stream()
                    .map(workspaceMapper::entityToResponse)
                    .toList();

            resultMap.put("status", SUCCESS);
            resultMap.put("msg", "워크스페이스 리스트 반환 성공!");
            resultMap.put("response", responseDTOList);

            return resultMap;
        } catch (Exception e) {
            throw new RuntimeException("워크스페이스 리스트 반환 중 오류가 발생했습니다.");
        }
    }

    @Override
    @Transactional(readOnly = true)
    public Map<String, Object> getWorkspacesByName(int page, int size, String sortField, String sortBy, String keyword) {
        Map<String, Object> resultMap = new HashMap<>();

        page = page < 1 ? 0 : page - 1;

        Pageable pageable = sortBy.equalsIgnoreCase("ASC")
                ? PageRequest.of(page, size, Sort.by(Sort.Order.asc(sortField)))
                : PageRequest.of(page, size, Sort.by(Sort.Order.desc(sortField)));

        try{
            List<WorkspaceResponseDTO> responseDTOList =
                    workspaceRepository.findByNameContainingIgnoreCase(keyword, pageable)
                            .stream()
                            .map(workspaceMapper::entityToResponse)
                            .toList();

            resultMap.put("status", SUCCESS);
            resultMap.put("msg", "워크스페이스 검색 리스트 반환 성공!");
            resultMap.put("response", responseDTOList);

            return resultMap;
        } catch (Exception e) {
            throw new RuntimeException("워크스페이스 검색 리스트 반환 중 오류가 발생했습니다.");
        }
    }

    @Override
    @Transactional
    public Map<String, Object> updateWorkspace(String id, List<CustomPatchOperation> patchOperations) {
        Map<String, Object> resultMap = new HashMap<>();

        Workspace workspace = workspaceRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("해당 ID의 워크스페이스를 찾을 수 없습니다."));

        log.info("patchOperations: {}", patchOperations);
        Workspace updatedWorkspace = applyCustomPatchToResource(patchOperations, workspace);

        workspaceRepository.save(updatedWorkspace);

        resultMap.put("status", SUCCESS);
        resultMap.put("msg", "워크스페이스 수정 성공!");
        resultMap.put("response", getWorkspaceById(id));

        return resultMap;
    }

    @Override
    @Transactional
    public Map<String, Object> deleteWorkspace(String id) {
        Map<String, Object> resultMap = new HashMap<>();

        if (!workspaceRepository.existsById(id)) {
            throw new ResourceNotFoundException("해당 ID의 워크스페이스를 찾을 수 없습니다.");
        }
        workspaceRepository.deleteById(id);

        resultMap.put("status", SUCCESS);
        resultMap.put("msg", "워크스페이스 삭제 성공!");
        resultMap.put("response", null);

        return resultMap;
    }

    private Workspace applyCustomPatchToResource(List<CustomPatchOperation> patchOperations, Workspace workspace) {
        for (CustomPatchOperation operation : patchOperations) {
            switch (operation.op()) {
                case "add":
                    processAddOperation(operation, workspace);
                    break;
                case "remove":
                    processRemoveOperation(operation, workspace);
                    break;
                case "replace":
                    workspace = processReplaceOperation(operation, workspace);
                    break;
                default:
                    log.info("Ignoring unsupported operation: {}", operation.op());
            }
        }
        return workspace;
    }

    /**
     * Handles add operations.
     * Expected path format: /users/{userId}
     */
    private void processAddOperation(CustomPatchOperation operation, Workspace workspace) {
        String path = operation.path();
        if (!path.startsWith("/users")) {
            log.info("Ignoring add op on non-user path: {}", path);
            return;
        }

        String[] parts = path.substring(1).split("/");
        if (parts.length < 2) {
            log.warn("Invalid add op path: {}. Expected format: /users/{userId}", path);
            return;
        }

        String userId = parts[1];
        log.info("Adding user with id: {}. Data: {} to userWorkspace", userId, operation.value());

        User user = userRepository.findByUserId(userId)
                .orElseThrow(() -> new ResourceNotFoundException("해당 ID의 사용자를 찾을 수 없습니다. ID: " + userId));

        UserPatchValue userPatchValue;
        try {
            userPatchValue = objectMapper.treeToValue(operation.value(), UserPatchValue.class);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
        String role = userPatchValue.role();

        UserWorkspace userWorkspace = UserWorkspace
                .builder()
                .user(user)
                .workspace(workspace)
                .role(role)
                .build();

        userWorkspaceRepository.save(userWorkspace);
    }

    /**
     * Handles remove operations.
     * Expected path format: /users/{userId}
     */
    private void processRemoveOperation(CustomPatchOperation operation, Workspace workspace) {
        String path = operation.path();
        if (!path.startsWith("/users")) {
            log.info("Ignoring remove op on non-user path: {}", path);
            return;
        }

        String[] parts = path.substring(1).split("/");
        if (parts.length < 2) {
            log.warn("Invalid remove op path: {}. Expected format: /users/{userId}", path);
            return;
        }

        String userId = parts[1];
        log.info("Removing user with id from userWorkspace: {}", userId);

        User user = userRepository.findByUserId(userId)
                .orElseThrow(() -> new ResourceNotFoundException("해당 ID의 사용자를 찾을 수 없습니다. ID: " + userId));

        userWorkspaceRepository.findByUserAndWorkspace(user, workspace).ifPresent(userWorkspaceRepository::delete);
    }

    /**
     * Handles replace operations.
     * Allowed paths:
     * - User operations: /users/{userId}/role (only 'role' attribute is updatable)
     * - Non-user operations: /description, /isActive, /tokenLimit
     */
    private Workspace processReplaceOperation(CustomPatchOperation operation, Workspace workspace) {
        String path = operation.path();
        // For replace op, valid prefixes are /users, /description, /isActive, or /tokenLimit
        if (!(path.startsWith("/users") ||
              path.startsWith("/description") ||
              path.startsWith("/isActive") ||
              path.startsWith("/tokenLimit"))) {
            log.info("Ignoring replace op on invalid path: {}", path);
            return workspace;
        }

        // Handle user-related replace operation
        if (path.startsWith("/users")) {
            String[] parts = path.substring(1).split("/");
            if (parts.length < 2) {
                log.warn("Invalid user path for replace op: {}. Expected format: /users/{userId}/...", path);
                return workspace;
            }

            String userId = parts[1];
            // Only 'role' property is supported for replacement on users
            if (parts.length >= 3 && "role".equalsIgnoreCase(parts[2])) {
                log.info("Replacing role for user with id: {} with value: {}", userId, operation.value());

                User user = userRepository.findByUserId(userId)
                        .orElseThrow(() -> new ResourceNotFoundException("해당 ID의 사용자를 찾을 수 없습니다. ID: " + userId));

                UserWorkspace userWorkspace = userWorkspaceRepository.findByUserAndWorkspace(user, workspace)
                        .orElseThrow(() -> new ResourceNotFoundException("해당 ID의 사용자를 워크스페이스에서 찾을 수 없습니다. ID: " + userId));

                String role = operation.value().toString().toUpperCase();
                if (role.equals("OWNER") || role.equals("MEMBER")) {
                    userWorkspace.setRole(role);
                } else {
                    log.warn("Ignoring replace op on invalid role: {}", role);
                }
            } else {
                log.warn("Ignoring replace op for unsupported attribute in user path: {}", path);
            }
        } else {
            // Handle non-user properties replacement
            log.info("Replacing non-user property at path: {} with value: {}", path, operation.value());
            String value = operation.value().toString();
            if (path.startsWith("/description")) {
                workspace.setDescription(value);
            } else if (path.startsWith("/isActive")) {
                workspace.setActive(Boolean.parseBoolean(value));
            } else if (path.startsWith("/tokenLimit")) {
                try {
                    int tokenLimit = Integer.parseInt(value);
                    workspace.setTokenLimit(tokenLimit);
                } catch (NumberFormatException e) {
                    log.warn("Invalid token limit value: {}", value);
                }
            } else {
                log.info("No handling implemented for path: {}", path);
            }
        }

        return workspace;
    }

}
