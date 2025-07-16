package com.blog.demo.repository;

import com.blog.demo.entity.Post;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface PostRepository extends JpaRepository<Post, Long> {
    List<Post> findAllByOrderByCreatedDateDesc();
    
    List<Post> findByTitleContainingIgnoreCaseOrderByCreatedDateDesc(String keyword);
    List<Post> findByContentContainingIgnoreCaseOrderByCreatedDateDesc(String keyword);
    List<Post> findByUserUsernameContainingIgnoreCaseOrderByCreatedDateDesc(String keyword);
    List<Post> findByTitleContainingIgnoreCaseOrContentContainingIgnoreCaseOrUserUsernameContainingIgnoreCaseOrderByCreatedDateDesc(String titleKeyword, String contentKeyword, String usernameKeyword);
    
    List<Post> findByCategoryOrderByCreatedDateDesc(String category);
}