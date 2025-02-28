package kr.co.proten.llmops.api.user.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import kr.co.proten.llmops.api.user.dto.request.PasswordUpdateDTO;
import kr.co.proten.llmops.api.user.dto.request.SignupDTO;
import kr.co.proten.llmops.api.user.dto.request.UserLoginDTO;
import kr.co.proten.llmops.api.user.dto.request.UserUpdateDTO;
import kr.co.proten.llmops.api.user.dto.response.AuthResponseDto;
import kr.co.proten.llmops.api.user.serivce.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@Slf4j
@Tag(name = "User", description = "유저 생성, 검색, 수정, 삭제하는 API")
@RequiredArgsConstructor
@RestController
@RequestMapping("/api/user")
public class UserController {

    private final UserService userService;
    private static final String SUCCESS = "success";

    // ADMIN 전용: 신규 사용자 생성 (가입)
    @PostMapping("/signup")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "사용자 생성 (관리자)", description = "사용자 생성 API")
    public ResponseEntity<Map<String, Object>> signup(@RequestBody SignupDTO signupDto) {
        Map<String, Object> resultMap = new HashMap<>();

        resultMap.put("status", SUCCESS);
        resultMap.put("msg", "회원가입 성공!");
        resultMap.put("response", userService.createUser(signupDto));

        return ResponseEntity.ok(resultMap);
    }

    // 로그인 – 모든 사용자 접근 가능
    @PostMapping("/login")
    @Operation(summary = "로그인", description = "사용자 ID/PW 기반 로그인 API")
    public ResponseEntity<Map<String, Object>> login(@RequestBody UserLoginDTO loginDto) {
        Map<String, Object> resultMap = new HashMap<>();

        resultMap.put("status", SUCCESS);
        resultMap.put("msg", "로그인 성공!");
        resultMap.put("response", userService.login(loginDto));

        return ResponseEntity.ok(resultMap);
    }

    // 로그아웃 – 모든 사용자 접근 가능 (accessToken, refreshToken 무효화 처리)
    @PostMapping("/logout")
    @Operation(summary = "로그아웃", description = "로그아웃 API")
    public ResponseEntity<Map<String, Object>> logout(HttpServletRequest request) {
        Map<String, Object> resultMap = new HashMap<>();

        String token = resolveToken(request);
        userService.logout(token);  // 내부에서 accessToken, refreshToken 무효화/삭제 처리
        resultMap.put("status", SUCCESS);
        resultMap.put("msg", "로그아웃 성공! 토큰이 삭제되었습니다.");
        resultMap.put("response", null);

        return ResponseEntity.ok(resultMap);
    }

    @PostMapping("/reissue")
    @Operation(summary = "액세스 토큰 갱신", description = "AccessToken 만료 시, RefreshToken 을 통해 갱신하는 API")
    public ResponseEntity<Map<String, Object>> reissueAccessToken(HttpServletRequest request) {
        Map<String, Object> resultMap = new HashMap<>();

        resultMap.put("status", SUCCESS);
        resultMap.put("msg", "토큰 갱신 완료!");
        resultMap.put("response", userService.reissueAccessToken(resolveToken(request)));

        return ResponseEntity.ok(resultMap);
    }

    // 사용자 정보 수정 – 본인 또는 ADMIN 접근 가능
    @PutMapping("/{userId}")
    @PreAuthorize("hasRole('ADMIN') or #userId == authentication.principal")
    @Operation(summary = "사용자 수정 (관리자 or 본인)", description = "사용자 정보 수정하는 API")
    public ResponseEntity<Map<String, Object>> updateUser(@PathVariable String userId,
                                                          @RequestBody UserUpdateDTO updateUserDto) {
        Map<String, Object> resultMap = new HashMap<>();

        resultMap.put("status", SUCCESS);
        resultMap.put("msg", "사용자 정보 수정 성공!");
        resultMap.put("response", userService.updateUser(userId, updateUserDto));

        return ResponseEntity.ok(resultMap);
    }

    // 사용자 비밀번호 변경 – 본인 또는 ADMIN 접근 가능
    @PutMapping("/{userId}/password")
    @PreAuthorize("hasRole('ADMIN') or #userId == authentication.principal")
    @Operation(summary = "비밀번호 수정 (관리자 or 본인)", description = "비밀번호 수정하는 API")
    public ResponseEntity<Map<String, Object>> updateUserPassword(@PathVariable String userId,
                                                          @RequestBody PasswordUpdateDTO passwordUpdateDTO) {
        Map<String, Object> resultMap = new HashMap<>();

        userService.updatePassword(userId, passwordUpdateDTO);

        resultMap.put("status", SUCCESS);
        resultMap.put("msg", "비밀번호 변경 성공!");
        resultMap.put("response", null);

        return ResponseEntity.ok(resultMap);
    }

    // 사용자 삭제 – ADMIN 전용
    @DeleteMapping("/{userId}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "사용자 삭제 (관리자)", description = "사용자ID로 사용자 삭제하는 API")
    public ResponseEntity<Map<String, Object>> deleteUser(@PathVariable String userId) {
        Map<String, Object> resultMap = new HashMap<>();

        userService.deleteUser(userId);
        resultMap.put("status", SUCCESS);
        resultMap.put("msg", "사용자 삭제 성공!");
        resultMap.put("response", null);

        return ResponseEntity.ok(resultMap);
    }

    // 단건 사용자 조회 – 본인 또는 ADMIN 접근 가능
    @GetMapping("/{userId}")
    @PreAuthorize("hasRole('ADMIN') or #userId == authentication.principal")
    @Operation(summary = "사용자 정보 조회 (관리자 or 본인)", description = "사용자 정보 조회하는 API")
    public ResponseEntity<Map<String, Object>> getUser(@PathVariable String userId) {
        Map<String, Object> resultMap = new HashMap<>();

        resultMap.put("status", SUCCESS);
        resultMap.put("msg", "사용자 조회 성공!");
        resultMap.put("response", userService.getUser(userId));

        return ResponseEntity.ok(resultMap);
    }

    // 전체 사용자 조회 – ADMIN 전용
    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "사용자 리스트 조회 (관리자)", description = "모든 사용자를 조회하는 API")
    public ResponseEntity<Map<String, Object>> getAllUsers(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "18") int size,
            @RequestParam(value = "sort_field", defaultValue = "createdAt") String sortField,
            @RequestParam(value = "sort_by", defaultValue = "desc") String sortBy
    ) {
        Map<String, Object> resultMap = new HashMap<>();

        resultMap.put("status", SUCCESS);
        resultMap.put("msg", "전체 사용자 조회 성공!");
        resultMap.put("response", userService.getAllUsers(page, size, sortField, sortBy));

        return ResponseEntity.ok(resultMap);
    }

    @GetMapping("/search")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "사용자 검색 (관리자)", description = "사용자 이름으로 검색하는 API")
    public ResponseEntity<Map<String, Object>> searchUsers(
            @RequestParam String keyword,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "18") int size,
            @RequestParam(value = "sort_field", defaultValue = "createdAt") String sortField,
            @RequestParam(value = "sort_by", defaultValue = "desc") String sortBy
    ) {
        Map<String, Object> resultMap = new HashMap<>();

        resultMap.put("status", SUCCESS);
        resultMap.put("msg", "사용자 검색 성공!");
        resultMap.put("response", userService.getUsersByName(page, size, sortField, sortBy, keyword));

        return ResponseEntity.ok(resultMap);
    }

    @GetMapping("/workspaces")
    @Operation(summary = "사용자의 워크스페이스 조회", description = "사용자가 속해 있는 워크스페이스 조회하는 API")
    public ResponseEntity<Map<String, Object>> getUserWorkspaces(@RequestHeader(value = "Authorization", required = false) String token) {
        log.info("auth:{}", token);
        Map<String, Object> resultMap = new HashMap<>();

        resultMap.put("status", SUCCESS);
        resultMap.put("msg", "워크스페이스 리스트 반환 성공!");
        resultMap.put("response", userService.getUserWorkspaces(token));

        return ResponseEntity.ok(resultMap);
    }

    @PostMapping("/select-workspace")
    @Operation(summary = "사용자의 워크스페이스 선택", description = "사용자가 선택한 워크스페이스로 새로운 토큰을 받는 API")
    public ResponseEntity<Map<String, Object>> selectWorkspace(
            @RequestHeader(value = "Authorization", required = false) String token,
            @RequestHeader("Workspace-Id") String workspaceId) {
        Map<String, Object> resultMap = new HashMap<>();

        resultMap.put("status", SUCCESS);
        resultMap.put("msg", "새로운 토큰 반환 성공!");
        resultMap.put("response", userService.selectWorkspace(token, workspaceId));

        return ResponseEntity.ok(resultMap);
    }

    private String resolveToken(HttpServletRequest request) {
        String bearerToken = request.getHeader("Authorization");
        if (StringUtils.hasText(bearerToken) && bearerToken.startsWith("Bearer ")) {
            return bearerToken.substring(7);
        }
        return null;
    }
}
