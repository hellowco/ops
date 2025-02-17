package kr.co.proten.llmops.api.workspace.service.impl;

import kr.co.proten.llmops.api.user.entity.User;
import kr.co.proten.llmops.api.user.entity.UserWorkspace;
import kr.co.proten.llmops.api.user.repository.UserRepository;
import kr.co.proten.llmops.api.user.repository.UserWorkspaceRepository;
import kr.co.proten.llmops.api.workspace.dto.request.WorkspaceCreateDTO;
import kr.co.proten.llmops.api.workspace.dto.response.WorkspaceResponseDTO;
import kr.co.proten.llmops.api.workspace.entity.Workspace;
import kr.co.proten.llmops.api.workspace.mapper.WorkspaceMapper;
import kr.co.proten.llmops.api.workspace.repository.WorkspaceRepository;
import kr.co.proten.llmops.api.workspace.service.WorkspaceService;
import kr.co.proten.llmops.core.exception.AppCreationException;
import kr.co.proten.llmops.core.exception.ResourceNotFoundException;
import kr.co.proten.llmops.core.exception.WorkspaceAlreadyExistException;
import kr.co.proten.llmops.core.exception.WorkspaceCreationException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.Map;
import java.util.List;
import java.util.Optional;

@Slf4j
@RequiredArgsConstructor
@Service
@Transactional
public class WorkspaceServiceImpl implements WorkspaceService {

    private final WorkspaceRepository workspaceRepository;
    private final WorkspaceMapper workspaceMapper;

    private static final String SUCCESS = "success";
    private final UserWorkspaceRepository userWorkspaceRepository;
    private final UserRepository userRepository;

    @Override
    @Transactional(readOnly = true)
    public Optional<Workspace> findWorkspaceById(String id){
        return workspaceRepository.findById(id);
    }

    @Override
    @Transactional
    public Map<String, Object> saveWorkspace(WorkspaceCreateDTO workspaceDTO) {
        Map<String, Object> resultMap = new HashMap<>();

        if (workspaceRepository.findByName(workspaceDTO.name()).isPresent()) {
            throw new WorkspaceAlreadyExistException("이미 존재하는 워크스페이스 이름입니다.");
        }

        try{
            Workspace workspace = workspaceMapper.requestToEntity(workspaceDTO);
            Workspace savedWS = workspaceRepository.save(workspace);

            User user = userRepository.findByUserId(workspaceDTO.workspaceOwner())
                    .orElseThrow(() -> new UsernameNotFoundException("User doesn't exist."));

            UserWorkspace userWorkspace = UserWorkspace.builder()
                    .user(user)
                    .workspace(workspace)
                    .role("OWNER")
                    .build();

            UserWorkspace savedUW = userWorkspaceRepository.save(userWorkspace);
            log.info("saved userWorkspace: {}", savedUW);

            resultMap.put("status", SUCCESS);
            resultMap.put("msg", "회원가입 성공!");
            resultMap.put("response", savedWS);

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

        resultMap.put("status", SUCCESS);
        resultMap.put("msg", "워크스페이스 반환 성공!");
        resultMap.put("response", workspaceMapper.entityToResponse(workspace));

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

    @Transactional(readOnly = true)
    @Override
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

   /* @Override
    public Map<String, Object> updateWorkspace(String id, WorkspaceUpdateDTO workspaceUpdateDTO) {
        Map<String, Object> resultMap = new HashMap<>();

        Workspace workspace = workspaceRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("해당 ID의 워크스페이스를 찾을 수 없습니다."));

        workspace.update(dto.getName(), dto.getDescription());
        Workspace updatedWorkspace = workspaceRepository.save(workspace);

        return new WorkspaceResponseDTO(
                updatedWorkspace.getId(),
                updatedWorkspace.getName(),
                updatedWorkspace.getDescription(),
                updatedWorkspace.getCreatedAt()
        );
    }*/

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
}
