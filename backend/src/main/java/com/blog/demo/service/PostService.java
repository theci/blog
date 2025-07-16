package com.blog.demo.service;

import com.blog.demo.dto.PostRequest;
import com.blog.demo.dto.PostResponse;
import com.blog.demo.entity.Post;
import com.blog.demo.entity.PostLike;
import com.blog.demo.entity.User;
import com.blog.demo.repository.PostLikeRepository;
import com.blog.demo.repository.PostRepository;
import com.blog.demo.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class PostService {
    
    @Autowired
    private PostRepository postRepository;
    
    @Autowired
    private UserRepository userRepository;
    
    @Autowired
    private PostLikeRepository postLikeRepository;
    
    public List<PostResponse> getAllPosts() {
        return postRepository.findByIsHiddenFalseOrderByCreatedDateDesc()
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
        post.setCategory(request.getCategory());
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
        post.setCategory(request.getCategory());
        
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
    
    public void incrementViewCount(Long postId) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();
        
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new RuntimeException("Post not found with id: " + postId));
        
        // 작성자 본인이 아닌 경우에만 조회수 증가
        if (username == null || "anonymousUser".equals(username) || 
            post.getUser() == null || !post.getUser().getUsername().equals(username)) {
            post.incrementViewCount();
            postRepository.save(post);
        }
    }
    
    public List<PostResponse> getPostsByCategory(String category) {
        return postRepository.findByCategoryAndIsHiddenFalseOrderByCreatedDateDesc(category)
                .stream()
                .map(PostResponse::new)
                .collect(Collectors.toList());
    }
    
    public void toggleLike(Long postId, String likeType) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();
        
        if (username == null || "anonymousUser".equals(username)) {
            throw new RuntimeException("User must be authenticated to like/dislike posts");
        }
        
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found: " + username));
        
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new RuntimeException("Post not found with id: " + postId));
        
        PostLike.LikeType newLikeType = "like".equalsIgnoreCase(likeType) ? 
                PostLike.LikeType.LIKE : PostLike.LikeType.DISLIKE;
        
        Optional<PostLike> existingLike = postLikeRepository.findByUserIdAndPostId(user.getId(), postId);
        
        if (existingLike.isPresent()) {
            PostLike like = existingLike.get();
            
            if (like.getLikeType() == newLikeType) {
                // 같은 버튼을 다시 누르면 취소
                if (newLikeType == PostLike.LikeType.LIKE) {
                    post.decrementLikeCount();
                } else {
                    post.decrementDislikeCount();
                }
                postLikeRepository.delete(like);
            } else {
                // 다른 버튼을 누르면 변경
                if (like.getLikeType() == PostLike.LikeType.LIKE) {
                    post.decrementLikeCount();
                    post.incrementDislikeCount();
                } else {
                    post.decrementDislikeCount();
                    post.incrementLikeCount();
                }
                like.setLikeType(newLikeType);
                postLikeRepository.save(like);
            }
        } else {
            // 새로운 좋아요/싫어요
            PostLike newLike = new PostLike();
            newLike.setUser(user);
            newLike.setPost(post);
            newLike.setLikeType(newLikeType);
            postLikeRepository.save(newLike);
            
            if (newLikeType == PostLike.LikeType.LIKE) {
                post.incrementLikeCount();
            } else {
                post.incrementDislikeCount();
            }
        }
        
        postRepository.save(post);
    }
    
    public List<PostResponse> getAllPostsSorted(String sortBy, String category) {
        List<Post> posts;
        
        if ("all".equals(category)) {
            switch (sortBy) {
                case "views":
                    posts = postRepository.findByIsHiddenFalseOrderByViewCountDesc();
                    break;
                case "popularity":
                    posts = postRepository.findByIsHiddenFalseOrderByPopularityDesc();
                    break;
                default:
                    posts = postRepository.findByIsHiddenFalseOrderByCreatedDateDesc();
                    break;
            }
        } else {
            switch (sortBy) {
                case "views":
                    posts = postRepository.findByCategoryAndIsHiddenFalseOrderByViewCountDesc(category);
                    break;
                case "popularity":
                    posts = postRepository.findByCategoryAndIsHiddenFalseOrderByPopularityDesc(category);
                    break;
                default:
                    posts = postRepository.findByCategoryAndIsHiddenFalseOrderByCreatedDateDesc(category);
                    break;
            }
        }
        
        return posts.stream()
                .map(PostResponse::new)
                .collect(Collectors.toList());
    }
}