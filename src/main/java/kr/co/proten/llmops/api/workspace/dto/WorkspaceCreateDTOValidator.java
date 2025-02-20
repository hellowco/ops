package kr.co.proten.llmops.api.workspace.dto;

import kr.co.proten.llmops.api.user.entity.User;
import kr.co.proten.llmops.api.user.repository.UserRepository;
import kr.co.proten.llmops.api.workspace.dto.request.WorkspaceRequestDTO;
import kr.co.proten.llmops.core.exception.InvalidInputException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class WorkspaceCreateDTOValidator {

    private static final String OWNER = "OWNER";
    private static final String MEMBER = "MEMBER";

    private final UserRepository userRepository;

    public void validate(WorkspaceRequestDTO dto) {
        // 1. tokenLimit 검증
        int tokenLimitValue = parseTokenLimit(dto.tokenLimit());

        // 2. 사용자 목록 검증
        List<WorkspaceRequestDTO.UserInfo> users = dto.users();
        if (users == null || users.isEmpty()) {
            throw new InvalidInputException("최소 한 명 이상의 사용자가 필요합니다.");
        }

        // 3. 존재하는 사용자인지 검증
        List<User> existingUsers = users.stream()
                .map(user -> userRepository.findByUserId(user.userId())
                        .orElseThrow(() -> new UsernameNotFoundException("User ID " + user.userId() + " doesn't exist.")))
                .toList();

        // 4. 각 사용자의 역할(role) 검증
        List<String> invalidUsers = users.stream()
                .filter(user -> !(OWNER.equalsIgnoreCase(user.role()) || MEMBER.equalsIgnoreCase(user.role())))
                .map(user -> "사용자 ID: " + user.userId() + ", role: " + user.role())
                .toList();

        if (!invalidUsers.isEmpty()) {
            throw new InvalidInputException("다음 사용자의 권한이 올바르지 않습니다: " + invalidUsers);
        }

        // 5. 적어도 한 명 이상의 OWNER 존재 여부 확인
        boolean hasOwner = users.stream()
                .anyMatch(user -> OWNER.equalsIgnoreCase(user.role()));
        if (!hasOwner) {
            throw new InvalidInputException("적어도 한 명 이상의 사용자는 OWNER 역할이어야 합니다.");
        }
    }

    private int parseTokenLimit(String tokenLimit) {
        try {
            int value = Integer.parseInt(tokenLimit);
            if (value <= 0) {
                throw new InvalidInputException("tokenLimit value must be greater than 0");
            }
            return value;
        } catch (NumberFormatException e) {
            throw new InvalidInputException("tokenLimit must be a valid integer");
        }
    }
}
