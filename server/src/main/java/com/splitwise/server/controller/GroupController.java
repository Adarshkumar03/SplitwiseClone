package com.splitwise.server.controller;

import com.splitwise.server.dto.AddUsersToGroupRequest;
import com.splitwise.server.dto.GroupDTO;
import com.splitwise.server.model.Group;
import com.splitwise.server.model.User;
import com.splitwise.server.service.GroupService;
import com.splitwise.server.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api")
public class GroupController {

    @Autowired
    private GroupService groupService;

    @Autowired
    private UserService userService;

    @GetMapping("/groups")
    public List<GroupDTO> getAllGroups() {
        return groupService.getAllGroups();
    }

    @GetMapping("/groups/{id}")
    public ResponseEntity<?> getGroup(@PathVariable Long id) {
        GroupDTO group = groupService.getGroupById(id);
        return ResponseEntity.ok(group);
    }

    @PostMapping("/group/{id}/users")
    public ResponseEntity<?> addUsersToGroup(@PathVariable Long id, @RequestBody AddUsersToGroupRequest request){
        try{
            Group updatedGroup = groupService.addUsersToGroup(id, request.getUserIds());
            return ResponseEntity.ok(Collections.singletonMap("message", "Users added to group successfully"));
        }catch(IllegalArgumentException e){
            return ResponseEntity.badRequest().body(Collections.singletonMap("error", e.getMessage()));
        }
    }

    @PostMapping("/groups")
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
}

