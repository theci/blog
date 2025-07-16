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
    private Long likeCount;
    private Long dislikeCount;
    private UserResponse userResponse;
    private List<FileAttachmentResponse> fileAttachments;
    private Boolean hidden;
    private String hiddenReason;
    private LocalDateTime hiddenDate;
    
    public PostResponse(Post post) {
        this.id = post.getId();
        this.title = post.getTitle();
        this.content = post.getContent();
        this.createdDate = post.getCreatedDate();
        this.modifiedDate = post.getModifiedDate();
        this.viewCount = post.getViewCount();
        this.category = post.getCategory();
        this.likeCount = post.getLikeCount();
        this.dislikeCount = post.getDislikeCount();
        this.userResponse = post.getUser() != null ? new UserResponse(post.getUser()) : null;
        this.fileAttachments = post.getFileAttachments() != null ? 
            post.getFileAttachments().stream()
                .map(FileAttachmentResponse::new)
                .collect(Collectors.toList()) : null;
        this.hidden = post.getIsHidden();
        this.hiddenReason = post.getHiddenReason();
        this.hiddenDate = post.getHiddenDate();
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
    
    public Long getLikeCount() {
        return likeCount;
    }
    
    public void setLikeCount(Long likeCount) {
        this.likeCount = likeCount;
    }
    
    public Long getDislikeCount() {
        return dislikeCount;
    }
    
    public void setDislikeCount(Long dislikeCount) {
        this.dislikeCount = dislikeCount;
    }
    
    public Boolean getHidden() {
        return hidden;
    }
    
    public void setHidden(Boolean hidden) {
        this.hidden = hidden;
    }
    
    public String getHiddenReason() {
        return hiddenReason;
    }
    
    public void setHiddenReason(String hiddenReason) {
        this.hiddenReason = hiddenReason;
    }
    
    public LocalDateTime getHiddenDate() {
        return hiddenDate;
    }
    
    public void setHiddenDate(LocalDateTime hiddenDate) {
        this.hiddenDate = hiddenDate;
    }
    
    public String getUsername() {
        return userResponse != null ? userResponse.getUsername() : null;
    }
}