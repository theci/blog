package com.blog.demo.service;

import com.blog.demo.dto.CommentRequest;
import com.blog.demo.dto.CommentResponse;
import com.blog.demo.entity.Comment;
import com.blog.demo.entity.Post;
import com.blog.demo.repository.CommentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class CommentService {
    
    @Autowired
    private CommentRepository commentRepository;
    
    @Autowired
    private PostService postService;
    
    public List<CommentResponse> getCommentsByPostId(Long postId) {
        return commentRepository.findByPostIdOrderByCreatedDateAsc(postId)
                .stream()
                .map(CommentResponse::new)
                .collect(Collectors.toList());
    }
    
    public CommentResponse createComment(Long postId, CommentRequest request) {
        Post post = postService.getPostEntityById(postId);
        
        Comment comment = new Comment();
        comment.setAuthor(request.getAuthor());
        comment.setContent(request.getContent());
        comment.setPost(post);
        
        Comment savedComment = commentRepository.save(comment);
        return new CommentResponse(savedComment);
    }
    
    public CommentResponse updateComment(Long commentId, CommentRequest request) {
        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new RuntimeException("Comment not found with id: " + commentId));
        
        comment.setAuthor(request.getAuthor());
        comment.setContent(request.getContent());
        
        Comment updatedComment = commentRepository.save(comment);
        return new CommentResponse(updatedComment);
    }
    
    public void deleteComment(Long commentId) {
        if (!commentRepository.existsById(commentId)) {
            throw new RuntimeException("Comment not found with id: " + commentId);
        }
        commentRepository.deleteById(commentId);
    }
    
    public long getCommentCount(Long postId) {
        return commentRepository.countByPostId(postId);
    }
}