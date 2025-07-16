package com.blog.demo.service;

import com.blog.demo.dto.PostRequest;
import com.blog.demo.dto.PostResponse;
import com.blog.demo.entity.Post;
import com.blog.demo.entity.User;
import com.blog.demo.repository.PostRepository;
import com.blog.demo.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class PostService {
    
    @Autowired
    private PostRepository postRepository;
    
    @Autowired
    private UserRepository userRepository;
    
    public List<PostResponse> getAllPosts() {
        return postRepository.findAllByOrderByCreatedDateDesc()
                .stream()
                .map(PostResponse::new)
                .collect(Collectors.toList());
    }
    
    public PostResponse getPostById(Long id) {
        Post post = postRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Post not found with id: " + id));
        return new PostResponse(post);
    }
    
    public Post getPostEntityById(Long id) {
        return postRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Post not found with id: " + id));
    }
    
    public PostResponse createPost(PostRequest request) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();
        
        if (username == null || "anonymousUser".equals(username)) {
            throw new RuntimeException("User must be authenticated to create posts");
        }
        
        User user = userRepository.findByUsername(username)
            .orElseThrow(() -> new RuntimeException("User not found: " + username));
        
        Post post = new Post();
        post.setTitle(request.getTitle());
        post.setContent(request.getContent());
        post.setUser(user);
        
        Post savedPost = postRepository.save(post);
        return new PostResponse(savedPost);
    }
    
    public PostResponse updatePost(Long id, PostRequest request) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();
        
        if (username == null || "anonymousUser".equals(username)) {
            throw new RuntimeException("User must be authenticated to update posts");
        }
        
        Post post = postRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Post not found with id: " + id));
        
        if (post.getUser() == null || !post.getUser().getUsername().equals(username)) {
            throw new RuntimeException("You can only edit your own posts");
        }
        
        post.setTitle(request.getTitle());
        post.setContent(request.getContent());
        
        Post updatedPost = postRepository.save(post);
        return new PostResponse(updatedPost);
    }
    
    public void deletePost(Long id) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();
        
        if (username == null || "anonymousUser".equals(username)) {
            throw new RuntimeException("User must be authenticated to delete posts");
        }
        
        Post post = postRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Post not found with id: " + id));
        
        if (post.getUser() == null || !post.getUser().getUsername().equals(username)) {
            throw new RuntimeException("You can only delete your own posts");
        }
        
        postRepository.deleteById(id);
    }
    
    public List<PostResponse> searchPosts(String keyword, String searchType) {
        List<Post> posts;
        
        switch (searchType.toLowerCase()) {
            case "title":
                posts = postRepository.findByTitleContainingIgnoreCaseOrderByCreatedDateDesc(keyword);
                break;
            case "content":
                posts = postRepository.findByContentContainingIgnoreCaseOrderByCreatedDateDesc(keyword);
                break;
            case "author":
                posts = postRepository.findByUserUsernameContainingIgnoreCaseOrderByCreatedDateDesc(keyword);
                break;
            default:
                posts = postRepository.findByTitleContainingIgnoreCaseOrContentContainingIgnoreCaseOrUserUsernameContainingIgnoreCaseOrderByCreatedDateDesc(keyword, keyword, keyword);
                break;
        }
        
        return posts.stream()
                .map(PostResponse::new)
                .collect(Collectors.toList());
    }
}