package kr.co.proten.llmops.api.workspace;

import kr.co.proten.llmops.api.user.entity.UserWorkspace;
import kr.co.proten.llmops.api.user.repository.UserWorkspaceRepository;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component("workspaceSecurity")
public class WorkspaceSecurity {

    private static final String OWNER = "owner";

    private final UserWorkspaceRepository userWorkspaceRepository;

    public WorkspaceSecurity(UserWorkspaceRepository userWorkspaceRepository) {
        this.userWorkspaceRepository = userWorkspaceRepository;
    }

    // 워크스페이스 소유자 여부를 확인하는 메서드.
    public boolean isOwner(String workspaceId) {
        // 현재 인증된 사용자 정보 획득
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()) {
            return false;
        }
        
        // 현재 사용자 ID를 얻거나 JWT에서 필요한 클레임을 가져옵니다.
        String currentUserId = authentication.getName();

        // 비즈니스 로직: workspaceId에 해당하는 워크스페이스에서 currentUserId가 소유자인지(user_workspace의 role이 OWNER인지) 확인합니다.
        return checkIfUserIsOwner(currentUserId, workspaceId);
    }

    private boolean checkIfUserIsOwner(String userId, String workspaceId) {
        Optional<UserWorkspace> userWorkspaceOpt = userWorkspaceRepository.findByUserIdAndWorkspaceId(userId, workspaceId);
        return userWorkspaceOpt.map(userWorkspace ->
                        OWNER.equalsIgnoreCase(userWorkspace.getRole()))
                .orElse(false);
    }
}
