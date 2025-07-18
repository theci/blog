package com.blog.demo.repository;

import com.blog.demo.entity.Post;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface PostRepository extends JpaRepository<Post, Long> {
    List<Post> findAllByOrderByCreatedDateDesc();
    List<Post> findByIsHiddenFalseOrderByCreatedDateDesc();
    
    List<Post> findByTitleContainingIgnoreCaseOrderByCreatedDateDesc(String keyword);
    List<Post> findByContentContainingIgnoreCaseOrderByCreatedDateDesc(String keyword);
    List<Post> findByUserUsernameContainingIgnoreCaseOrderByCreatedDateDesc(String keyword);
    List<Post> findByTitleContainingIgnoreCaseOrContentContainingIgnoreCaseOrUserUsernameContainingIgnoreCaseOrderByCreatedDateDesc(String titleKeyword, String contentKeyword, String usernameKeyword);
    
    List<Post> findByCategoryOrderByCreatedDateDesc(String category);
    List<Post> findByCategoryAndIsHiddenFalseOrderByCreatedDateDesc(String category);
    List<Post> findAllByOrderByViewCountDesc();
    List<Post> findByIsHiddenFalseOrderByViewCountDesc();
    List<Post> findByCategoryOrderByViewCountDesc(String category);
    List<Post> findByCategoryAndIsHiddenFalseOrderByViewCountDesc(String category);
    
    @Query("SELECT p FROM Post p ORDER BY (p.likeCount - p.dislikeCount) DESC, p.createdDate DESC")
    List<Post> findAllByOrderByPopularityDesc();
    
    @Query("SELECT p FROM Post p WHERE p.isHidden = false ORDER BY (p.likeCount - p.dislikeCount) DESC, p.createdDate DESC")
    List<Post> findByIsHiddenFalseOrderByPopularityDesc();
    
    @Query("SELECT p FROM Post p WHERE p.category = :category ORDER BY (p.likeCount - p.dislikeCount) DESC, p.createdDate DESC")
    List<Post> findByCategoryOrderByPopularityDesc(@Param("category") String category);
    
    @Query("SELECT p FROM Post p WHERE p.category = :category AND p.isHidden = false ORDER BY (p.likeCount - p.dislikeCount) DESC, p.createdDate DESC")
    List<Post> findByCategoryAndIsHiddenFalseOrderByPopularityDesc(@Param("category") String category);
}