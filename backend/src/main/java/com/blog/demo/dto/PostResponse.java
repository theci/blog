package com.blog.demo.dto;

import com.blog.demo.entity.Post;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

public class PostResponse {
    private Long id;
    private String title;
    private String content;
    private LocalDateTime createdDate;
    private LocalDateTime modifiedDate;
    private Long viewCount;
    private String category;
    private UserResponse userResponse;
    private List<FileAttachmentResponse> fileAttachments;
    
    public PostResponse(Post post) {
        this.id = post.getId();
        this.title = post.getTitle();
        this.content = post.getContent();
        this.createdDate = post.getCreatedDate();
        this.modifiedDate = post.getModifiedDate();
        this.viewCount = post.getViewCount();
        this.category = post.getCategory();
        this.userResponse = post.getUser() != null ? new UserResponse(post.getUser()) : null;
        this.fileAttachments = post.getFileAttachments() != null ? 
            post.getFileAttachments().stream()
                .map(FileAttachmentResponse::new)
                .collect(Collectors.toList()) : null;
    }
    
    public Long getId() {
        return id;
    }
    
    public void setId(Long id) {
        this.id = id;
    }
    
    public String getTitle() {
        return title;
    }
    
    public void setTitle(String title) {
        this.title = title;
    }
    
    public String getContent() {
        return content;
    }
    
    public void setContent(String content) {
        this.content = content;
    }
    
    public LocalDateTime getCreatedDate() {
        return createdDate;
    }
    
    public void setCreatedDate(LocalDateTime createdDate) {
        this.createdDate = createdDate;
    }
    
    public LocalDateTime getModifiedDate() {
        return modifiedDate;
    }
    
    public void setModifiedDate(LocalDateTime modifiedDate) {
        this.modifiedDate = modifiedDate;
    }
    
    public UserResponse getUserResponse() {
        return userResponse;
    }
    
    public void setUserResponse(UserResponse userResponse) {
        this.userResponse = userResponse;
    }
    
    public List<FileAttachmentResponse> getFileAttachments() {
        return fileAttachments;
    }
    
    public void setFileAttachments(List<FileAttachmentResponse> fileAttachments) {
        this.fileAttachments = fileAttachments;
    }
    
    public Long getViewCount() {
        return viewCount;
    }
    
    public void setViewCount(Long viewCount) {
        this.viewCount = viewCount;
    }
    
    public String getCategory() {
        return category;
    }
    
    public void setCategory(String category) {
        this.category = category;
    }
}