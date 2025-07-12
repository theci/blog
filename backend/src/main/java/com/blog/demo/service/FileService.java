package com.blog.demo.service;

import com.blog.demo.entity.FileAttachment;
import com.blog.demo.entity.Post;
import com.blog.demo.repository.FileAttachmentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;

import java.io.IOException;
import java.util.UUID;

@Service
public class FileService {
    
    private static final String BUCKET_NAME = "pch-250712";
    
    @Autowired
    private S3Client s3Client;
    
    @Autowired
    private FileAttachmentRepository fileAttachmentRepository;
    
    public FileAttachment uploadFile(MultipartFile file, Post post) throws IOException {
        String originalFileName = file.getOriginalFilename();
        String fileExtension = originalFileName.substring(originalFileName.lastIndexOf("."));
        String s3Key = "blog-files/" + UUID.randomUUID().toString() + fileExtension;
        
        PutObjectRequest putObjectRequest = PutObjectRequest.builder()
                .bucket(BUCKET_NAME)
                .key(s3Key)
                .contentType(file.getContentType())
                .build();
        
        s3Client.putObject(putObjectRequest, RequestBody.fromInputStream(file.getInputStream(), file.getSize()));
        
        String s3Url = String.format("https://%s.s3.ap-northeast-2.amazonaws.com/%s", BUCKET_NAME, s3Key);
        
        FileAttachment fileAttachment = new FileAttachment();
        fileAttachment.setOriginalFileName(originalFileName);
        fileAttachment.setS3Key(s3Key);
        fileAttachment.setS3Url(s3Url);
        fileAttachment.setContentType(file.getContentType());
        fileAttachment.setFileSize(file.getSize());
        fileAttachment.setPost(post);
        
        return fileAttachmentRepository.save(fileAttachment);
    }
}