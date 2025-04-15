package com.splitwise.server.repo;

import com.splitwise.server.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepo extends JpaRepository<User, Long> {
    Optional<User> findByEmail(String email);

    @Query("""
    SELECT u FROM User u
    WHERE u.id NOT IN (
        SELECT ug.user.id FROM UserGroup ug WHERE ug.group.id = :groupId
    )
""")
    List<User> findUsersNotInGroup(@Param("groupId") Long groupId);
}