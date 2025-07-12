package com.blog.demo.service;

import com.blog.demo.dto.CommentRequest;
import com.blog.demo.dto.CommentResponse;
import com.blog.demo.entity.Comment;
import com.blog.demo.entity.Post;
import com.blog.demo.entity.User;
import com.blog.demo.repository.CommentRepository;
import com.blog.demo.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class CommentService {
    
    @Autowired
    private CommentRepository commentRepository;
    
    @Autowired
    private PostService postService;
    
    @Autowired
    private UserRepository userRepository;
    
    public List<CommentResponse> getCommentsByPostId(Long postId) {
        return commentRepository.findByPostIdOrderByCreatedDateAsc(postId)
                .stream()
                .map(CommentResponse::new)
                .collect(Collectors.toList());
    }
    
    public CommentResponse createComment(Long postId, CommentRequest request) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();
        
        User user = userRepository.findByUsername(username)
            .orElseThrow(() -> new RuntimeException("User not found"));
        
        Post post = postService.getPostEntityById(postId);
        
        Comment comment = new Comment();
        comment.setAuthor(user.getDisplayName() != null ? user.getDisplayName() : user.getUsername());
        comment.setContent(request.getContent());
        comment.setPost(post);
        comment.setUser(user);
        
        Comment savedComment = commentRepository.save(comment);
        return new CommentResponse(savedComment);
    }
    
    public CommentResponse updateComment(Long commentId, CommentRequest request) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();
        
        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new RuntimeException("Comment not found with id: " + commentId));
        
        if (comment.getUser() == null || !comment.getUser().getUsername().equals(username)) {
            throw new RuntimeException("You can only edit your own comments");
        }
        
        comment.setContent(request.getContent());
        
        Comment updatedComment = commentRepository.save(comment);
        return new CommentResponse(updatedComment);
    }
    
    public void deleteComment(Long commentId) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();
        
        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new RuntimeException("Comment not found with id: " + commentId));
        
        if (comment.getUser() == null || !comment.getUser().getUsername().equals(username)) {
            throw new RuntimeException("You can only delete your own comments");
        }
        
        commentRepository.deleteById(commentId);
    }
    
    public long getCommentCount(Long postId) {
        return commentRepository.countByPostId(postId);
    }
}