package com.blog.demo.dto;

import com.blog.demo.entity.FileAttachment;
import java.time.LocalDateTime;

public class FileAttachmentResponse {
    private Long id;
    private String originalFileName;
    private String s3Url;
    private String contentType;
    private Long fileSize;
    private LocalDateTime createdDate;
    
    public FileAttachmentResponse(FileAttachment fileAttachment) {
        this.id = fileAttachment.getId();
        this.originalFileName = fileAttachment.getOriginalFileName();
        this.s3Url = fileAttachment.getS3Url();
        this.contentType = fileAttachment.getContentType();
        this.fileSize = fileAttachment.getFileSize();
        this.createdDate = fileAttachment.getCreatedDate();
    }
    
    public Long getId() {
        return id;
    }
    
    public void setId(Long id) {
        this.id = id;
    }
    
    public String getOriginalFileName() {
        return originalFileName;
    }
    
    public void setOriginalFileName(String originalFileName) {
        this.originalFileName = originalFileName;
    }
    
    public String getS3Url() {
        return s3Url;
    }
    
    public void setS3Url(String s3Url) {
        this.s3Url = s3Url;
    }
    
    public String getContentType() {
        return contentType;
    }
    
    public void setContentType(String contentType) {
        this.contentType = contentType;
    }
    
    public Long getFileSize() {
        return fileSize;
    }
    
    public void setFileSize(Long fileSize) {
        this.fileSize = fileSize;
    }
    
    public LocalDateTime getCreatedDate() {
        return createdDate;
    }
    
    public void setCreatedDate(LocalDateTime createdDate) {
        this.createdDate = createdDate;
    }
}