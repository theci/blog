package com.blog.demo.service;

import com.blog.demo.dto.*;
import com.blog.demo.entity.User;
import com.blog.demo.entity.UserSuspension;
import com.blog.demo.repository.UserRepository;
import com.blog.demo.repository.UserSuspensionRepository;
import com.blog.demo.security.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;
import java.util.Optional;

@Service
public class AuthService {
    
    @Autowired
    private UserRepository userRepository;
    
    @Autowired
    private PasswordEncoder passwordEncoder;
    
    @Autowired
    private AuthenticationManager authenticationManager;
    
    @Autowired
    private JwtUtil jwtUtil;
    
    @Autowired
    private UserSuspensionRepository userSuspensionRepository;
    
    public AuthResponse register(RegisterRequest request) {
        if (userRepository.existsByUsername(request.getUsername())) {
            throw new RuntimeException("Username already exists");
        }
        
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new RuntimeException("Email already exists");
        }
        
        User user = new User();
        user.setUsername(request.getUsername());
        user.setEmail(request.getEmail());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setDisplayName(request.getDisplayName() != null ? request.getDisplayName() : request.getUsername());
        
        User savedUser = userRepository.save(user);
        String token = jwtUtil.generateTokenFromUsername(savedUser.getUsername());
        
        return new AuthResponse(token, new UserResponse(savedUser));
    }
    
    public AuthResponse login(LoginRequest request) {
        User user = userRepository.findByUsername(request.getUsername())
            .orElseThrow(() -> new RuntimeException("User not found"));
        
        // 사용자 정지 상태 확인
        Optional<UserSuspension> suspension = userSuspensionRepository.findActiveSuspensionByUserId(user.getId(), LocalDateTime.now());
        if (suspension.isPresent()) {
            UserSuspension userSuspension = suspension.get();
            String message = userSuspension.isPermanent() ? 
                "계정이 영구 정지되었습니다." : 
                "계정이 " + userSuspension.getEndDate() + "까지 정지되었습니다.";
            throw new RuntimeException(message);
        }
        
        Authentication authentication = authenticationManager.authenticate(
            new UsernamePasswordAuthenticationToken(
                request.getUsername(),
                request.getPassword()
            )
        );

        SecurityContextHolder.getContext().setAuthentication(authentication);
        String jwt = jwtUtil.generateToken(authentication);
        
        user.addPoints(10);
        userRepository.save(user);
        
        return new AuthResponse(jwt, new UserResponse(user));
    }
    
    public UserResponse getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();
        
        User user = userRepository.findByUsername(username)
            .orElseThrow(() -> new RuntimeException("User not found"));
        
        return new UserResponse(user);
    }
    
    public java.util.List<UserResponse> getAllUsers() {
        return userRepository.findAll().stream()
            .map(UserResponse::new)
            .collect(java.util.stream.Collectors.toList());
    }
}