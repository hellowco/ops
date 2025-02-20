package kr.co.proten.llmops.api.workspace.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import kr.co.proten.llmops.api.workspace.dto.request.CustomPatchOperation;
import kr.co.proten.llmops.api.workspace.dto.request.WorkspaceRequestDTO;
import kr.co.proten.llmops.api.workspace.service.WorkspaceService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

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
    public ResponseEntity<Map<String, Object>> createWorkspace(@RequestBody WorkspaceRequestDTO workspaceRequestDTO) {
        Map<String, Object> resultMap;

        resultMap = workspaceService.saveWorkspace(workspaceRequestDTO);

        return ResponseEntity.ok().body(resultMap);
    }

   /* @PutMapping("/{workspaceId}")
    @PreAuthorize("hasRole('ADMIN') or @workspaceSecurity.isOwner(#workspaceId)")
    public ResponseEntity<Map<String, Object>> updateWorkspace(
            @PathVariable String workspaceId,
            @RequestBody WorkspaceRequestDTO workspaceRequestDTO
    ) {
        Map<String, Object> resultMap;

        resultMap = workspaceService.updateWorkspace(workspaceId, workspaceRequestDTO);

        return ResponseEntity.ok(resultMap);
    }*/
   @PatchMapping(value = "/{workspaceId}", consumes = "application/json-patch+json")
   @PreAuthorize("hasRole('ADMIN') or @workspaceSecurity.isOwner(#workspaceId)")
   public ResponseEntity<Map<String, Object>> updateWorkspace(
           @PathVariable String workspaceId,
           @RequestBody List<CustomPatchOperation> patchOperations
   ) {
       Map<String, Object> resultMap;

       resultMap = workspaceService.updateWorkspace(workspaceId, patchOperations);

       return ResponseEntity.ok().body(resultMap);
   }

    @DeleteMapping("/{workspaceId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Map<String, Object>> deleteWorkspace(@PathVariable String workspaceId) {
        Map<String, Object> resultMap;

        resultMap = workspaceService.deleteWorkspace(workspaceId);

        return ResponseEntity.ok().body(resultMap);
    }

    @GetMapping("/{workspaceId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Map<String, Object>> getWorkspace(@PathVariable String workspaceId) {
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
        Map<String, Object> resultMap;

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
