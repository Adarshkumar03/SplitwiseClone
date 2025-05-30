package com.splitwise.server.service;

import com.splitwise.server.dto.GroupDTO;
import com.splitwise.server.dto.UserDTO;
import com.splitwise.server.model.Group;
import com.splitwise.server.model.User;
import com.splitwise.server.model.UserGroup;
import com.splitwise.server.repo.GroupRepo;
import com.splitwise.server.repo.TransactionRepo;
import com.splitwise.server.repo.UserGroupRepo;
import com.splitwise.server.repo.UserRepo;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class GroupService {
    private final GroupRepo repo;
    private final UserGroupRepo userGroupRepo;
    private final UserRepo userRepo;
    private final TransactionRepo transactionRepo;
    TransactionService transactionService;

    @Autowired
    public GroupService(GroupRepo groupRepo, UserGroupRepo userGroupRepo, UserRepo userRepo, TransactionRepo transactionRepo, TransactionService transactionService) {
        this.repo = groupRepo;
        this.userGroupRepo = userGroupRepo;
        this.userRepo = userRepo;
        this.transactionRepo = transactionRepo;
        this.transactionService = transactionService;
    }

    public List<GroupDTO> getUserGroups(Long userId) {
        return repo.findByUserGroups_User_Id(userId).stream()
                .map(group -> new GroupDTO(group.getId(), group.getName(), Collections.emptyList())) // No members here
                .collect(Collectors.toList());
    }

    public GroupDTO getGroupById(Long id) {
        Group group = repo.findById(id).orElseThrow(() -> new RuntimeException("Group not found"));

        List<UserDTO> members = transactionRepo.getTotalOwedPerUser(group.getId()).stream()
                .map(row -> new UserDTO(
                        ((Number) row[0]).longValue(),
                        (String) row[1],
                        (BigDecimal) row[2]
                ))
                .collect(Collectors.toList());

        return new GroupDTO(group.getId(), group.getName(), members);
    }

    public boolean existsByName(String name) {
        return repo.existsByName(name);
    }

    @Transactional
    public Group addGroup(Group group, User user) {
        UserGroup userGroup = new UserGroup();
        userGroup.setUser(user);
        userGroup.setGroup(group);

        Group savedGroup = repo.save(group);

        userGroupRepo.save(userGroup);

        return savedGroup;
    }


    public Group addUsersToGroup(Long groupId, List<Long> userIds) {
        Group group = repo.findById(groupId)
                .orElseThrow(() -> new IllegalArgumentException("Group not found"));

        List<User> users = userRepo.findAllById(userIds);
        if (users.isEmpty()) {
            throw new IllegalArgumentException("No valid users found to add");
        }

        Set<Long> existingUserIds = group.getUserGroups()
                .stream()
                .map(userGroup -> userGroup.getUser().getId())
                .collect(Collectors.toSet());

        for (User user : users) {
            if (!existingUserIds.contains(user.getId())) {
                UserGroup userGroup = new UserGroup();
                userGroup.setUser(user);
                userGroup.setGroup(group);

                group.getUserGroups().add(userGroup);
                user.getUserGroups().add(userGroup);

                userGroupRepo.save(userGroup);
            }
        }

        return repo.save(group);
    }

    public List<Group> getGroupsUserNotIn(Long userId) {
        return repo.findGroupsUserNotIn(userId);
    }

    public void joinGroup(Long userId, Long groupId) throws Exception {
        Group group = repo.findById(groupId).orElseThrow(() -> new Exception("Group not found"));
        User user = userRepo.findById(userId).orElseThrow(() -> new Exception("User not found"));

        // Check if user is already part of the group
        Optional<UserGroup> existingUserGroup = userGroupRepo.findByUserAndGroup(user, group);
        if (existingUserGroup.isPresent()) {
            throw new Exception("User is already a member of this group");
        }

        UserGroup userGroup = new UserGroup();
        userGroup.setUser(user);
        userGroup.setGroup(group);
        userGroupRepo.save(userGroup);
    }

    public void leaveGroup(Long userId, Long groupId) throws Exception {
        Group group = repo.findById(groupId)
                .orElseThrow(() -> new Exception("Group not found"));
        User user = userRepo.findById(userId)
                .orElseThrow(() -> new Exception("User not found"));

        Optional<UserGroup> existingUserGroup = userGroupRepo.findByUserAndGroup(user, group);
        if (existingUserGroup.isEmpty()) {
            throw new Exception("User is not a member of this group");
        }

        userGroupRepo.deleteByUserAndGroup(user, group);
    }

}
