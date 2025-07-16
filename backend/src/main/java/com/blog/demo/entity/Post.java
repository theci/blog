package com.blog.demo.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "posts")
public class Post {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(nullable = false, length = 200)
    private String title;
    
    @Column(nullable = false, columnDefinition = "TEXT")
    private String content;
    
    @Column(name = "created_date")
    private LocalDateTime createdDate;
    
    @Column(name = "modified_date")
    private LocalDateTime modifiedDate;
    
    @Column(name = "view_count", nullable = false, columnDefinition = "BIGINT DEFAULT 0")
    private Long viewCount = 0L;
    
    @Column(name = "category", nullable = false, length = 50)
    private String category;
    
    @Column(name = "like_count", nullable = false, columnDefinition = "BIGINT DEFAULT 0")
    private Long likeCount = 0L;
    
    @Column(name = "dislike_count", nullable = false, columnDefinition = "BIGINT DEFAULT 0")
    private Long dislikeCount = 0L;
    
    @Column(name = "is_hidden", nullable = false, columnDefinition = "BOOLEAN DEFAULT false")
    private Boolean isHidden = false;
    
    @Column(name = "hidden_by")
    private String hiddenBy;
    
    @Column(name = "hidden_date")
    private LocalDateTime hiddenDate;
    
    @Column(name = "hidden_reason", length = 500)
    private String hiddenReason;
    
    @OneToMany(mappedBy = "post", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<FileAttachment> fileAttachments;
    
    @OneToMany(mappedBy = "post", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Comment> comments;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;
    
    @PrePersist
    public void prePersist() {
        this.createdDate = LocalDateTime.now();
        this.modifiedDate = LocalDateTime.now();
    }
    
    @PreUpdate
    public void preUpdate() {
        this.modifiedDate = LocalDateTime.now();
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
    
    public List<FileAttachment> getFileAttachments() {
        return fileAttachments;
    }
    
    public void setFileAttachments(List<FileAttachment> fileAttachments) {
        this.fileAttachments = fileAttachments;
    }
    
    public List<Comment> getComments() {
        return comments;
    }
    
    public void setComments(List<Comment> comments) {
        this.comments = comments;
    }
    
    public User getUser() {
        return user;
    }
    
    public void setUser(User user) {
        this.user = user;
    }
    
    public Long getViewCount() {
        return viewCount;
    }
    
    public void setViewCount(Long viewCount) {
        this.viewCount = viewCount;
    }
    
    public void incrementViewCount() {
        this.viewCount++;
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
    
    public void incrementLikeCount() {
        this.likeCount++;
    }
    
    public void decrementLikeCount() {
        if (this.likeCount > 0) {
            this.likeCount--;
        }
    }
    
    public void incrementDislikeCount() {
        this.dislikeCount++;
    }
    
    public void decrementDislikeCount() {
        if (this.dislikeCount > 0) {
            this.dislikeCount--;
        }
    }
    
    public Long getPopularityScore() {
        return this.likeCount - this.dislikeCount;
    }
    
    public Boolean getIsHidden() {
        return isHidden;
    }
    
    public void setIsHidden(Boolean isHidden) {
        this.isHidden = isHidden;
    }
    
    public String getHiddenBy() {
        return hiddenBy;
    }
    
    public void setHiddenBy(String hiddenBy) {
        this.hiddenBy = hiddenBy;
    }
    
    public LocalDateTime getHiddenDate() {
        return hiddenDate;
    }
    
    public void setHiddenDate(LocalDateTime hiddenDate) {
        this.hiddenDate = hiddenDate;
    }
    
    public String getHiddenReason() {
        return hiddenReason;
    }
    
    public void setHiddenReason(String hiddenReason) {
        this.hiddenReason = hiddenReason;
    }
    
    public void hide(String hiddenBy, String reason) {
        this.isHidden = true;
        this.hiddenBy = hiddenBy;
        this.hiddenDate = LocalDateTime.now();
        this.hiddenReason = reason;
    }
    
    public void unhide() {
        this.isHidden = false;
        this.hiddenBy = null;
        this.hiddenDate = null;
        this.hiddenReason = null;
    }
}