package kr.co.proten.llmops.api.workspace.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import kr.co.proten.llmops.api.user.dto.request.SignupDTO;
import kr.co.proten.llmops.api.user.dto.request.UserLoginDTO;
import kr.co.proten.llmops.api.user.dto.request.UserUpdateDTO;
import kr.co.proten.llmops.api.user.mapper.UserMapper;
import kr.co.proten.llmops.api.user.serivce.UserService;
import kr.co.proten.llmops.api.workspace.dto.request.WorkspaceCreateDTO;
import kr.co.proten.llmops.api.workspace.service.WorkspaceService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@Tag(name = "Workspace", description = "워크스페이스 생성, 검색, 수정, 삭제하는 API")
@RequiredArgsConstructor
@RestController
@RequestMapping("/api/workspace")
public class WorkspaceController {

    private final WorkspaceService workspaceService;

    @PostMapping("/")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Map<String, Object>> createWorkspace(@RequestBody WorkspaceCreateDTO workspaceCreateDTO) {
        Map<String, Object> resultMap;

        workspaceCreateDTO.validate();
        resultMap = workspaceService.saveWorkspace(workspaceCreateDTO);

        return ResponseEntity.ok().body(resultMap);
    }

/*    // 사용자 정보 수정 – 본인 또는 ADMIN 접근 가능
    @PutMapping("/")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Map<String, Object>> updateWorkspace(@RequestBody UserUpdateDTO updateUserDto) {
        Map<String, Object> resultMap = new HashMap<>();

        resultMap.put("status", "SUCCESS");
        resultMap.put("msg", "사용자 정보 수정 성공!");
        resultMap.put("response", userService.updateUser(userId, updateUserDto));

        return ResponseEntity.ok(resultMap);
    }*/

    @DeleteMapping("/{workspaceId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Map<String, Object>> deleteUser(@PathVariable String workspaceId) {
        Map<String, Object> resultMap;

        resultMap = workspaceService.deleteWorkspace(workspaceId);

        return ResponseEntity.ok().body(resultMap);
    }

    @GetMapping("/{workspaceId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Map<String, Object>> getUser(@PathVariable String workspaceId) {
        Map<String, Object> resultMap;

        resultMap = workspaceService.getWorkspaceById(workspaceId);

        return ResponseEntity.ok().body(resultMap);
    }

    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Map<String, Object>> getAllWorkspaces(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "18") int size,
            @RequestParam(value = "sort_field", defaultValue = "createdAt") String sortField,
            @RequestParam(value = "sort_by", defaultValue = "desc") String sortBy
    ) {
        Map<String, Object> resultMap = new HashMap<>();

        resultMap = workspaceService.getAllWorkspaces(page, size, sortField, sortBy);

        return ResponseEntity.ok(resultMap);
    }

    @Operation(summary = "이름으로 워크스페이스 검색 (페이지네이션 지원)")
    @GetMapping("/search")
    public ResponseEntity<Map<String, Object>> searchWorkspaces(
            @RequestParam String keyword,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(value = "sort_field", defaultValue = "createdAt") String sortField,
            @RequestParam(value = "sort_by", defaultValue = "desc") String sortBy
    ) {
        return ResponseEntity.ok(workspaceService.getWorkspacesByName(page, size, sortField, sortBy, keyword));
    }

}
