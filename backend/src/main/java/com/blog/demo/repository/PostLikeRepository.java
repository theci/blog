package com.blog.demo.repository;

import com.blog.demo.entity.PostLike;
import com.blog.demo.entity.PostLike.LikeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;

@Repository
public interface PostLikeRepository extends JpaRepository<PostLike, Long> {
    Optional<PostLike> findByUserIdAndPostId(Long userId, Long postId);
    long countByPostIdAndLikeType(Long postId, LikeType likeType);
    void deleteByUserIdAndPostId(Long userId, Long postId);
}