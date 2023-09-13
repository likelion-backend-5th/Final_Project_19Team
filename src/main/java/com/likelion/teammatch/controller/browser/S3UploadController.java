package com.likelion.teammatch.controller.browser;

import com.likelion.teammatch.service.S3UploadService;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@Controller
public class S3UploadController {
    private final S3UploadService s3UploadService;

    public S3UploadController(S3UploadService s3UploadService) {
        this.s3UploadService = s3UploadService;
    }

    @PostMapping("/api/upload")
    public ResponseEntity<String> uploadImageToS3(@RequestParam("file") MultipartFile file) {
        try {
            // 이미지를 S3에 업로드하고 업로드된 이미지 URL을 반환합니다.
            String imageUrl = s3UploadService.upload(file);
            return ResponseEntity.ok(imageUrl);
        } catch (IOException e) {
            // 업로드 실패 시 예외 처리
            e.printStackTrace();
            return ResponseEntity.status(500).body("Failed to upload image.");
        }
    }
}