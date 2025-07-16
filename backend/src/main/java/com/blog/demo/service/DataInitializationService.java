package com.blog.demo.service;

import com.blog.demo.entity.User;
import com.blog.demo.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class DataInitializationService implements CommandLineRunner {

    @Autowired
    private UserRepository userRepository;
    
    @Autowired
    private PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) throws Exception {
        createDefaultAdminUser();
    }

    private void createDefaultAdminUser() {
        // 기본 관리자 계정이 없으면 생성
        if (!userRepository.existsByUsername("admin")) {
            User admin = new User();
            admin.setUsername("admin");
            admin.setEmail("admin@example.com");
            admin.setPassword(passwordEncoder.encode("admin"));
            admin.setDisplayName("Administrator");
            admin.setRole(User.Role.ADMIN);
            
            User savedAdmin = userRepository.save(admin);
            System.out.println("기본 관리자 계정이 생성되었습니다: admin/admin");
            System.out.println("생성된 관리자 정보: " + savedAdmin.getUsername() + ", Role: " + savedAdmin.getRole());
        } else {
            // 기존 admin 계정 정보 확인
            User existingAdmin = userRepository.findByUsername("admin").orElse(null);
            if (existingAdmin != null) {
                System.out.println("기존 관리자 계정 정보: " + existingAdmin.getUsername() + ", Role: " + existingAdmin.getRole());
            }
        }
    }
}