package kr.co.proten.llmops.api.workspace;

import kr.co.proten.llmops.api.app.entity.AppEntity;
import kr.co.proten.llmops.api.app.repository.AppRepository;
import kr.co.proten.llmops.api.user.entity.UserWorkspace;
import kr.co.proten.llmops.api.user.repository.UserWorkspaceRepository;
import kr.co.proten.llmops.api.workflow.entity.Workflow;
import kr.co.proten.llmops.api.workflow.repository.WorkflowRepository;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

@Component("workspaceSecurity")
public class WorkspaceSecurity {

    private static final String OWNER = "OWNER";

    private final UserWorkspaceRepository userWorkspaceRepository;
    private final AppRepository appRepository;

    public WorkspaceSecurity(UserWorkspaceRepository userWorkspaceRepository, AppRepository appRepository) {
        this.userWorkspaceRepository = userWorkspaceRepository;
        this.appRepository = appRepository;
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

    // 워크스페이스 소속 여부를 확인하는 메서드.
    public boolean isUserInWorkspace(String workflowOrAppId) {
        // 현재 인증된 사용자 정보 획득
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()) {
            return false;
        }

        // 현재 사용자 ID를 얻거나 JWT에서 필요한 클레임을 가져옵니다.
        String currentUserId = authentication.getName();

        // 비즈니스 로직: workspaceId에 해당하는 워크스페이스에서 currentUserId가 소유자인지(user_workspace의 role이 OWNER인지) 확인합니다.
        return checkIfUserBelongsToWorkspace(currentUserId, workflowOrAppId);
    }

    // List<String> 형태로 여러 앱 ID 또는 workflow ID를 받아서 검증하는 오버로딩 메서드
    public boolean isUserInWorkspace(List<String> idList) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()) {
            return false;
        }
        String currentUserId = authentication.getName();
        // 각 ID에 대해 checkIfUserBelongsToWorkspace를 호출하여 모두 통과해야 true 반환
        for (String id : idList) {
            if (!checkIfUserBelongsToWorkspace(currentUserId, id)) {
                return false;
            }
        }
        return true;
    }

    // 기존의 검증 로직: 전달된 id가 workflowId 또는 appId에 해당하는지 확인한 후,
    // 해당 앱의 워크스페이스를 찾아 user_workspace 테이블에 현재 사용자가 소속되어 있는지 확인
    private boolean checkIfUserBelongsToWorkspace(String userId, String id) {
        // 먼저 workflowId로 앱을 조회합니다.
        Optional<AppEntity> appOpt = appRepository.findByWorkflow_WorkflowId(id);

        // workflowId로 앱을 찾지 못하면, appId로 조회해봅니다.
        if (appOpt.isEmpty()) {
            appOpt = appRepository.findById(id);
        }

        if (appOpt.isPresent()) {
            // 조회된 앱에서 workspaceId를 얻습니다.
            String workspaceId = appOpt.get().getWorkspace().getWorkspaceId();
            // user_workspace 테이블에서 해당 workspace에 사용자가 속해있는지 확인합니다.
            Optional<UserWorkspace> userWorkspaceOpt = userWorkspaceRepository.findByUserIdAndWorkspaceId(userId, workspaceId);
            return userWorkspaceOpt.isPresent();
        } else {
            // 만약 앱으로 조회되지 않았다면, id가 워크스페이스 ID일 수 있으므로 직접 확인
            Optional<UserWorkspace> userWorkspaceOpt = userWorkspaceRepository.findByUserIdAndWorkspaceId(userId, id);
            return userWorkspaceOpt.isPresent();
        }
    }

    private boolean checkIfUserIsOwner(String userId, String workspaceId) {
        Optional<UserWorkspace> userWorkspaceOpt = userWorkspaceRepository.findByUserIdAndWorkspaceId(userId, workspaceId);
        return userWorkspaceOpt.map(userWorkspace ->
                        OWNER.equalsIgnoreCase(userWorkspace.getRole()))
                .orElse(false);
    }
}
