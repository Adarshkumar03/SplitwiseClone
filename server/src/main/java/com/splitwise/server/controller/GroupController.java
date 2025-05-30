package com.splitwise.server.controller;

import com.splitwise.server.dto.AddUsersToGroupRequest;
import com.splitwise.server.dto.BasicUserDTO;
import com.splitwise.server.dto.GroupDTO;
import com.splitwise.server.model.Group;
import com.splitwise.server.model.User;
import com.splitwise.server.service.GroupService;
import com.splitwise.server.service.TransactionService;
import com.splitwise.server.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.Collections;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/groups")
public class GroupController {

    @Autowired
    private GroupService groupService;

    @Autowired
    private UserService userService;

    @Autowired
    private TransactionService transactionService;

    @GetMapping
    public ResponseEntity<List<GroupDTO>> getUserGroups() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null || !authentication.isAuthenticated() || authentication.getPrincipal() == "anonymousUser") {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Collections.emptyList());
        }

        User user = (User) authentication.getPrincipal();
        List<GroupDTO> groups = groupService.getUserGroups(user.getId());

        return ResponseEntity.ok(groups);
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getGroupById(@PathVariable Long id) {
        GroupDTO group = groupService.getGroupById(id);
        return ResponseEntity.ok(group);
    }

    @GetMapping("/{id}/available-users")
    public ResponseEntity<?> getNonGroupMembers(@PathVariable Long id) {
        try {
            List<User> users = userService.getUsersNotInGroup(id); // Service layer
            List<BasicUserDTO> dtos = users.stream()
                    .map(user -> new BasicUserDTO(user.getId(), user.getName()))
                    .toList();
            return ResponseEntity.ok(dtos);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Collections.singletonMap("error", "Failed to fetch users not in group"));
        }
    }

    @PostMapping("/{id}/users")
    public ResponseEntity<?> addUsersToGroup(@PathVariable Long id, @RequestBody AddUsersToGroupRequest request){
        try{
            Group updatedGroup = groupService.addUsersToGroup(id, request.getUserIds());
            return ResponseEntity.ok(Collections.singletonMap("message", "Users added to group successfully"));
        }catch(IllegalArgumentException e){
            return ResponseEntity.badRequest().body(Collections.singletonMap("error", e.getMessage()));
        }
    }

    @PostMapping
    public ResponseEntity<?> addGroup(@RequestBody Group group, Authentication authentication) {
        if (authentication == null || authentication.getName() == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Collections.singletonMap("error", "User is not authenticated"));
        }

        try {
            String email = authentication.getName();
            User user = userService.getUserByEmail(email);

            if (user == null) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body(Collections.singletonMap("error", "User not found"));
            }

            if (groupService.existsByName(group.getName())) {
                return ResponseEntity.status(HttpStatus.CONFLICT)
                        .body(Collections.singletonMap("error", "Group name already exists"));
            }

            Group savedGroup = groupService.addGroup(group, user);
            return ResponseEntity.status(HttpStatus.CREATED).body(savedGroup);

        } catch (DataIntegrityViolationException e) {
            return ResponseEntity.status(HttpStatus.CONFLICT)
                    .body(Collections.singletonMap("error", "Group name already exists"));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Collections.singletonMap("error", "Failed to create group"));
        }
    }

    @GetMapping("/potential")
    public ResponseEntity<?> getJoinableGroups() {
        User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if (user == null) {
            return ResponseEntity.status(401).body(Map.of("message", "User not authenticated"));
        }

        try {
            return ResponseEntity.ok(groupService.getGroupsUserNotIn(user.getId()));
        } catch (Exception e) {
            return ResponseEntity.status(500).body(Map.of("message", "Error retrieving join able groups"));
        }
    }

    @PostMapping("/{groupId}/join")
    public ResponseEntity<?> joinGroup(@PathVariable Long groupId) {
        User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        if (user == null) {
            return ResponseEntity.status(401).body(Map.of("message", "User not authenticated"));
        }

        try {
            groupService.joinGroup(user.getId(), groupId);
            return ResponseEntity.ok(Map.of("message", "Successfully joined the group"));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("message", e.getMessage()));
        }
    }

    @DeleteMapping("/{groupId}/leave")
    public ResponseEntity<?> leaveGroup(@PathVariable Long groupId) {
        User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if (user == null) {
            return ResponseEntity.status(401).body(Map.of("message", "User not authenticated"));
        }

        try {
            groupService.leaveGroup(user.getId(), groupId);
            return ResponseEntity.ok(Map.of("message", "Left group successfully"));
        } catch (IllegalStateException e) {
            return ResponseEntity.status(400).body(Map.of("message", e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(500).body(Map.of("message", "Error leaving group"));
        }
    }

}

