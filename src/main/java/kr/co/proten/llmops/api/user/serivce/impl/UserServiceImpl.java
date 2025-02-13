package kr.co.proten.llmops.api.user.serivce.impl;

import kr.co.proten.llmops.api.auth.entity.RefreshToken;
import kr.co.proten.llmops.api.auth.repository.RefreshTokenRepository;
import kr.co.proten.llmops.api.auth.serivce.JwtService;
import kr.co.proten.llmops.api.user.dto.request.SignupDTO;
import kr.co.proten.llmops.api.user.dto.request.UserLoginDTO;
import kr.co.proten.llmops.api.user.dto.request.UserUpdateDTO;
import kr.co.proten.llmops.api.user.dto.response.AuthResponseDto;
import kr.co.proten.llmops.api.user.dto.response.UserDTO;
import kr.co.proten.llmops.api.user.entity.User;
import kr.co.proten.llmops.api.user.entity.UserWorkspace;
import kr.co.proten.llmops.api.user.mapper.UserMapper;
import kr.co.proten.llmops.api.user.repository.UserRepository;
import kr.co.proten.llmops.api.user.repository.UserWorkspaceRepository;
import kr.co.proten.llmops.api.user.serivce.UserService;
import kr.co.proten.llmops.core.exception.UserAlreadyExistException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final UserWorkspaceRepository userWorkspaceRepository;
    private final RefreshTokenRepository refreshTokenRepository;
    private final PasswordEncoder passwordEncoder;
    private final UserMapper userMapper;
    private final JwtService jwtService;

    @Override
    @Transactional
    public UserDTO createUser(SignupDTO dto) {
        // 이미 존재하는 사용자 확인
        if (userRepository.existsById(dto.getUserId())) {
            throw new UserAlreadyExistException("User already exists");
        }
        // SSO 환경에서 기본 생성자를 사용하여 User 생성 (username 기반 email 자동 생성, 기본 비밀번호 설정)
        User user = new User(dto.getUserId(), dto.getUsername(), passwordEncoder);
        // 별도 비밀번호가 입력된 경우 override
        if (StringUtils.hasText(dto.getPassword())) {
            user.setPassword(passwordEncoder.encode(dto.getPassword()));
        }
        // role 지정 (ADMIN 전용 작업이므로 컨트롤러에서 추가 검증)
        if (StringUtils.hasText(dto.getRole())) {
            user.setRole(dto.getRole());
        }
        return userMapper.fromEntity(userRepository.save(user));
    }

    @Override
    @Transactional
    public AuthResponseDto login(UserLoginDTO dto) {
        User user = userRepository.findByUserId(dto.userId())
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));

        if (!passwordEncoder.matches(dto.password(), user.getPassword())) {
            throw new BadCredentialsException("Invalid credentials");
        }

        // access token 발급 (워크스페이스 선택 전이므로 workspaceId 미포함)
        String accessToken = jwtService.generateBasicAccessToken(
                user.getUserId(),
                user.getUsername(),
                user.getRole()
        );
        // refresh token 발급
        String refreshTokenStr = jwtService.generateRefreshToken(user.getUserId());

        // refresh token 저장 또는 업데이트
        RefreshToken refreshToken = refreshTokenRepository.findById(user.getUserId())
                .orElse(new RefreshToken(user.getUserId(), refreshTokenStr));
        refreshToken.updateToken(refreshTokenStr);
        refreshTokenRepository.save(refreshToken);

        return new AuthResponseDto(user.getUserId(), user.getUsername(), accessToken, refreshTokenStr);
    }

    @Override
    @Transactional
    public void logout(String token) {
        // access token에서 userId 추출 (유효한 토큰이라는 가정 하에)
        String userId = jwtService.extractUserId(token);
        // refresh token 엔티티 삭제 (토큰 무효화)
        refreshTokenRepository.deleteById(userId);
    }

    @Override
    @Transactional
    public UserDTO updateUser(String userId, UserUpdateDTO dto) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));

        if (StringUtils.hasText(dto.getUsername())) {
            user.setUsername(dto.getUsername());
            // email은 username 기반으로 자동 재생성
            user.setEmail(dto.getUsername() + "@proten.co.kr");
        }
        if (StringUtils.hasText(dto.getPassword())) {
            user.setPassword(passwordEncoder.encode(dto.getPassword()));
        }
        return userMapper.fromEntity(userRepository.save(user));
    }

    @Override
    @Transactional
    public void deleteUser(String userId) {
        if (!userRepository.existsById(userId)) {
            throw new UsernameNotFoundException("User not found");
        }
        userRepository.deleteById(userId);
        // 사용자 삭제 시 refresh token도 삭제
        refreshTokenRepository.deleteById(userId);
    }

    @Override
    @Transactional(readOnly = true)
    public UserDTO getUser(String userId) {
        return userMapper
                .fromEntity(userRepository.findById(userId)
                        .orElseThrow(() -> new UsernameNotFoundException("User not found")));
    }

    @Override
    @Transactional(readOnly = true)
    public List<UserDTO> getAllUsers(int page, int size, String sortField, String sortBy) {
        page = page < 1 ? 0 : page - 1;

        Pageable pageable = sortBy.equalsIgnoreCase("ASC")
                ? PageRequest.of(page, size, Sort.by(Sort.Order.asc(sortField)))
                : PageRequest.of(page, size, Sort.by(Sort.Order.desc(sortField)));

        return userRepository.findAll(pageable)
                .stream()
                .map(userMapper::fromEntity)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<String> getUserWorkspaces(String token) {
        String userId = jwtService.extractUserId(token);
        return userWorkspaceRepository.findWorkspaceIdsByUserId(userId);
    }

    @Override
    @Transactional(readOnly = true)
    public AuthResponseDto selectWorkspace(String token, String workspaceId) {
        // JWT 토큰에서 userId 추출 (User 엔티티의 id는 String 타입입니다)
        String userId = jwtService.extractUserId(token);

        // User 엔티티 조회: 없으면 인증 문제로 간주
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new BadCredentialsException("Invalid token: user not found"));

        String username = user.getUsername();
        String globalRole = user.getRole(); // User 엔티티의 역할 필드 사용

        // UserWorkspace 조회: 사용자가 해당 워크스페이스에 소속되어 있지 않으면 권한 문제로 간주
        Optional<UserWorkspace> userWorkspaceOpt = userWorkspaceRepository
                .findByUserIdAndWorkspaceId(userId, workspaceId);

        if (userWorkspaceOpt.isEmpty()) {
            throw new AccessDeniedException("User does not belong to this workspace");
        }

        // UserWorkspace 엔티티에 저장된 워크스페이스 내 역할 사용 (필드명이 role)
        String workspaceRole = userWorkspaceOpt.get().getRole();

        // 새로운 워크스페이스 관련 access token 생성
        String newAccessToken = jwtService.generateWorkspaceAccessToken(
                userId, username, globalRole, workspaceId, workspaceRole);

        return new AuthResponseDto(userId, username, newAccessToken, null);
    }

}
