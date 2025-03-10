package kr.co.proten.llmops.api.user.repository;

import kr.co.proten.llmops.api.user.entity.User;
import kr.co.proten.llmops.api.user.entity.UserWorkspace;
import kr.co.proten.llmops.api.workspace.entity.Workspace;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserWorkspaceRepository extends JpaRepository<UserWorkspace, Long> {
    @Query("SELECT uw.workspace FROM UserWorkspace uw WHERE uw.user.userId = :userId")
    List<Workspace> findWorkspacesByUserId(@Param("userId") String userId);

    @Query("SELECT uw FROM UserWorkspace uw " +
           "WHERE uw.user.userId = :userId AND uw.workspace.workspaceId = :workspaceId")
    Optional<UserWorkspace> findByUserIdAndWorkspaceId(@Param("userId") String userId,
                                                       @Param("workspaceId") String workspaceId);

    @Query("SELECT uw FROM UserWorkspace uw " +
           "WHERE uw.workspace.workspaceId = :workspaceId")
    List<UserWorkspace> findUserByWorkspaceId(@Param("workspaceId") String workspaceId);

    Optional<UserWorkspace> findByUserAndWorkspace(User user, Workspace workspace);
}
