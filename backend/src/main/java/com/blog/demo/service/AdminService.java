package com.blog.demo.service;

import com.blog.demo.entity.Post;
import com.blog.demo.entity.User;
import com.blog.demo.entity.UserSuspension;
import com.blog.demo.repository.PostRepository;
import com.blog.demo.repository.UserRepository;
import com.blog.demo.repository.UserSuspensionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class AdminService {
    
    @Autowired
    private UserRepository userRepository;
    
    @Autowired
    private PostRepository postRepository;
    
    @Autowired
    private UserSuspensionRepository userSuspensionRepository;
    
    public List<User> getAllUsers() {
        return userRepository.findAll();
    }
    
    public List<Post> getAllPosts() {
        return postRepository.findAllByOrderByCreatedDateDesc();
    }
    
    public void suspendUser(Long userId, int days, String reason) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String adminUsername = authentication.getName();
        
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));
        
        // 기존 활성 정지 해제
        Optional<UserSuspension> existingSuspension = userSuspensionRepository.findByUserIdAndIsActiveTrue(userId);
        if (existingSuspension.isPresent()) {
            existingSuspension.get().setIsActive(false);
            userSuspensionRepository.save(existingSuspension.get());
        }
        
        // 새 정지 생성
        UserSuspension suspension = new UserSuspension();
        suspension.setUser(user);
        suspension.setSuspendedBy(adminUsername);
        suspension.setReason(reason);
        suspension.setStartDate(LocalDateTime.now());
        
        if (days > 0) {
            suspension.setEndDate(LocalDateTime.now().plusDays(days));
        } // days가 0 이하면 영구정지 (endDate가 null)
        
        userSuspensionRepository.save(suspension);
    }
    
    public void unsuspendUser(Long userId) {
        Optional<UserSuspension> suspension = userSuspensionRepository.findByUserIdAndIsActiveTrue(userId);
        if (suspension.isPresent()) {
            suspension.get().setIsActive(false);
            userSuspensionRepository.save(suspension.get());
        }
    }
    
    public void hidePost(Long postId, String reason) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String adminUsername = authentication.getName();
        
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new RuntimeException("Post not found"));
        
        post.hide(adminUsername, reason);
        postRepository.save(post);
    }
    
    public void unhidePost(Long postId) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new RuntimeException("Post not found"));
        
        post.unhide();
        postRepository.save(post);
    }
    
    public void deletePost(Long postId) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new RuntimeException("Post not found"));
        
        postRepository.delete(post);
    }
    
    public boolean isUserSuspended(Long userId) {
        Optional<UserSuspension> suspension = userSuspensionRepository.findActiveSuspensionByUserId(userId, LocalDateTime.now());
        return suspension.isPresent();
    }
    
    public UserSuspension getUserSuspension(Long userId) {
        return userSuspensionRepository.findActiveSuspensionByUserId(userId, LocalDateTime.now()).orElse(null);
    }
    
    public User getUserByUsername(String username) {
        return userRepository.findByUsername(username).orElse(null);
    }
}