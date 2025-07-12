package com.blog.demo.controller;

import com.blog.demo.dto.FileAttachmentResponse;
import com.blog.demo.entity.FileAttachment;
import com.blog.demo.entity.Post;
import com.blog.demo.service.FileService;
import com.blog.demo.service.PostService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/api/files")
@CrossOrigin(origins = "*")
public class FileController {
    
    @Autowired
    private FileService fileService;
    
    @Autowired
    private PostService postService;
    
    @PostMapping("/upload/{postId}")
    public ResponseEntity<List<FileAttachmentResponse>> uploadFiles(
            @PathVariable Long postId,
            @RequestParam("files") MultipartFile[] files) {
        
        try {
            Post post = postService.getPostEntityById(postId);
            List<FileAttachmentResponse> uploadedFiles = new ArrayList<>();
            
            for (MultipartFile file : files) {
                if (!file.isEmpty()) {
                    FileAttachment fileAttachment = fileService.uploadFile(file, post);
                    uploadedFiles.add(new FileAttachmentResponse(fileAttachment));
                }
            }
            
            return ResponseEntity.ok(uploadedFiles);
        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }
}