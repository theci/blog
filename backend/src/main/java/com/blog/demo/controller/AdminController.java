package com.blog.demo.controller;

import com.blog.demo.dto.PostResponse;
import com.blog.demo.dto.UserResponse;
import com.blog.demo.entity.Post;
import com.blog.demo.entity.User;
import com.blog.demo.entity.UserSuspension;
import com.blog.demo.service.AdminService;
import com.blog.demo.service.CustomUserDetailsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/admin")
@CrossOrigin(origins = "*")
public class AdminController {
    
    @Autowired
    private AdminService adminService;
    
    @Autowired
    private CustomUserDetailsService userDetailsService;
    
    @GetMapping("/users")
    public ResponseEntity<List<UserResponse>> getAllUsers() {
        try {
            // 관리자 권한 확인
            if (!isAdmin()) {
                System.out.println("Access denied: Not admin");
                return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
            }
            
            List<User> users = adminService.getAllUsers();
            List<UserResponse> userResponses = users.stream()
                    .map(UserResponse::new)
                    .collect(java.util.stream.Collectors.toList());
            return ResponseEntity.ok(userResponses);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
    
    @GetMapping("/posts")
    public ResponseEntity<List<PostResponse>> getAllPosts() {
        try {
            // 관리자 권한 확인
            if (!isAdmin()) {
                System.out.println("Access denied: Not admin");
                return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
            }
            
            List<Post> posts = adminService.getAllPosts();
            List<PostResponse> postResponses = posts.stream()
                    .map(PostResponse::new)
                    .collect(java.util.stream.Collectors.toList());
            return ResponseEntity.ok(postResponses);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
    
    @PostMapping("/users/{userId}/suspend")
    public ResponseEntity<Void> suspendUser(@PathVariable Long userId, @RequestBody Map<String, Object> request) {
        try {
            if (!isAdmin()) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
            }
            
            int days = (Integer) request.get("days");
            String reason = (String) request.get("reason");
            adminService.suspendUser(userId, days, reason);
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
    
    @PostMapping("/users/{userId}/unsuspend")
    public ResponseEntity<Void> unsuspendUser(@PathVariable Long userId) {
        try {
            if (!isAdmin()) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
            }
            adminService.unsuspendUser(userId);
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
    
    @PostMapping("/posts/{postId}/hide")
    public ResponseEntity<Void> hidePost(@PathVariable Long postId, @RequestBody Map<String, String> request) {
        try {
            if (!isAdmin()) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
            }
            String reason = request.get("reason");
            adminService.hidePost(postId, reason);
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
    
    @PostMapping("/posts/{postId}/unhide")
    public ResponseEntity<Void> unhidePost(@PathVariable Long postId) {
        try {
            if (!isAdmin()) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
            }
            adminService.unhidePost(postId);
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
    
    @DeleteMapping("/posts/{postId}")
    public ResponseEntity<Void> deletePost(@PathVariable Long postId) {
        try {
            if (!isAdmin()) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
            }
            adminService.deletePost(postId);
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
    
    @GetMapping("/users/{userId}/suspension")
    public ResponseEntity<UserSuspension> getUserSuspension(@PathVariable Long userId) {
        try {
            if (!isAdmin()) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
            }
            UserSuspension suspension = adminService.getUserSuspension(userId);
            if (suspension != null) {
                return ResponseEntity.ok(suspension);
            } else {
                return ResponseEntity.notFound().build();
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
    
    private boolean isAdmin() {
        try {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            if (authentication == null || !authentication.isAuthenticated()) {
                System.out.println("No authentication or not authenticated");
                return false;
            }
            
            String username = authentication.getName();
            if ("anonymousUser".equals(username)) {
                System.out.println("Anonymous user");
                return false;
            }
            
            System.out.println("Username: " + username);
            System.out.println("Authorities: " + authentication.getAuthorities());
            
            // 사용자 권한 확인
            boolean hasAdminRole = authentication.getAuthorities().stream()
                    .anyMatch(authority -> {
                        System.out.println("Checking authority: " + authority.getAuthority());
                        return authority.getAuthority().equals("ROLE_ADMIN");
                    });
            
            System.out.println("Has ROLE_ADMIN: " + hasAdminRole);
            
            // 추가로 데이터베이스에서 사용자 정보 확인
            try {
                User user = adminService.getUserByUsername(username);
                if (user != null) {
                    System.out.println("User from DB: " + user.getUsername() + ", Role: " + user.getRole());
                    return user.getRole().name().equals("ADMIN");
                }
            } catch (Exception e) {
                System.out.println("Error fetching user from DB: " + e.getMessage());
            }
            
            return hasAdminRole;
        } catch (Exception e) {
            System.out.println("Error checking admin role: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }
}