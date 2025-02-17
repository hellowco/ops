package kr.co.proten.llmops.api.auth.serivce;

import io.jsonwebtoken.Claims;
import kr.co.proten.llmops.core.exception.WorkspaceMismatchException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.util.StringUtils;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Collections;
import java.util.List;

@Slf4j
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtService jwtService;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

        // Authorization 헤더에서 "Bearer " 토큰 추출
        String token = resolveToken(request);

        if (StringUtils.hasText(token) && jwtService.validateToken(token)) {
            // JWT에서 클레임 추출
            Claims claims = jwtService.extractClaims(token);
            String userId = claims.getSubject();
            String role = claims.get("role", String.class);

            // Spring Security의 권한(ROLE_) 설정
            List<SimpleGrantedAuthority> authorities =
                    Collections.singletonList(new SimpleGrantedAuthority("ROLE_" + role));

            // 인증 객체 생성 (비밀번호는 JWT 기반 인증이므로 null)
            UsernamePasswordAuthenticationToken authentication =
                    new UsernamePasswordAuthenticationToken(userId, null, authorities);
            authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

            // 인증 정보를 SecurityContext에 저장
            SecurityContextHolder.getContext().setAuthentication(authentication);

            // ADMIN은 모든 워크스페이스 접근 가능하므로 workspace 검증 생략
            if (!"ADMIN".equalsIgnoreCase(role)) {
                // JWT에 workspaceId가 포함되어 있다면 처리
                String tokenWorkspaceId = claims.get("workspaceId", String.class);
                if (StringUtils.hasText(tokenWorkspaceId)) {
                    // 클라이언트에서 전송한 "Workspace-Id" 헤더 값 추출
                    String headerWorkspaceId = request.getHeader("Workspace-Id");

                    // 헤더에 Workspace-Id가 있을 경우, JWT의 workspaceId와 일치하는지 검증
                    if (StringUtils.hasText(headerWorkspaceId)) {
                        if (!headerWorkspaceId.equals(tokenWorkspaceId)) {
                            // 불일치 시 커스텀 예외 발생: 전역 예외 처리기로 전달됨
                            throw new WorkspaceMismatchException("Workspace ID mismatch");
                        }
                    }
                    // 검증 성공 또는 헤더가 없을 경우, JWT의 workspaceId를 request attribute로 설정
                    request.setAttribute("workspaceId", tokenWorkspaceId);
                }
            }
        }

        filterChain.doFilter(request, response);
    }

    public String resolveToken(HttpServletRequest request) {
        String bearerToken = request.getHeader("Authorization");
        if (StringUtils.hasText(bearerToken) && bearerToken.startsWith("Bearer ")) {
            return bearerToken.substring(7);
        }
        return null;
    }
}
