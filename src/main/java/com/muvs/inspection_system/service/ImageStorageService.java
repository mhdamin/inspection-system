package com.muvs.inspection_system.service;

import com.muvs.inspection_system.config.AwsS3Config;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.*;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.UUID;

@Slf4j
@Service
@ConditionalOnBean(S3Client.class)
public class ImageStorageService {
    private final S3Client s3Client;
    private final AwsS3Config awsS3Config;

    public ImageStorageService(S3Client s3Client, AwsS3Config awsS3Config) {
        this.s3Client = s3Client;
        this.awsS3Config = awsS3Config;
    }
    
    /**
     * Upload file to S3 and return the public URL
     */
    public String uploadFile(MultipartFile file, String folderPath) throws IOException {
        if (file.isEmpty()) {
            throw new IllegalArgumentException("File is empty");
        }
        
        String fileName = generateUniqueFileName(file.getOriginalFilename());
        String key = folderPath + "/" + fileName;
        
        log.info("Uploading file to S3: bucket={}, key={}", awsS3Config.getBucketName(), key);
        
        try {
            PutObjectRequest putObjectRequest = PutObjectRequest.builder()
                    .bucket(awsS3Config.getBucketName())
                    .key(key)
                    .contentType(file.getContentType())
                    .contentLength(file.getSize())
                    .build();
            
            s3Client.putObject(putObjectRequest, RequestBody.fromInputStream(file.getInputStream(), file.getSize()));
            
            String fileUrl = getFileUrl(key);
            log.info("File uploaded successfully: {}", fileUrl);
            
            return fileUrl;
        } catch (S3Exception e) {
            log.error("Failed to upload file to S3: {}", e.getMessage(), e);
            throw new RuntimeException("Failed to upload file to S3: " + e.getMessage(), e);
        }
    }
    
    /**
     * Delete file from S3
     */
    public void deleteFile(String fileUrl) {
        try {
            String key = extractKeyFromUrl(fileUrl);
            
            log.info("Deleting file from S3: bucket={}, key={}", awsS3Config.getBucketName(), key);
            
            DeleteObjectRequest deleteObjectRequest = DeleteObjectRequest.builder()
                    .bucket(awsS3Config.getBucketName())
                    .key(key)
                    .build();
            
            s3Client.deleteObject(deleteObjectRequest);
            log.info("File deleted successfully from S3: {}", key);
        } catch (S3Exception e) {
            log.error("Failed to delete file from S3: {}", e.getMessage(), e);
            throw new RuntimeException("Failed to delete file from S3: " + e.getMessage(), e);
        }
    }
    
    /**
     * Generate unique file name with timestamp and UUID
     */
    private String generateUniqueFileName(String originalFileName) {
        String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss"));
        String uuid = UUID.randomUUID().toString().substring(0, 8);
        String extension = getFileExtension(originalFileName);
        return timestamp + "_" + uuid + extension;
    }
    
    /**
     * Get file extension from original file name
     */
    private String getFileExtension(String fileName) {
        if (fileName == null || fileName.lastIndexOf('.') == -1) {
            return "";
        }
        return fileName.substring(fileName.lastIndexOf('.'));
    }
    
    /**
     * Construct the public URL for the uploaded file
     */
    private String getFileUrl(String key) {
        return String.format("https://%s.s3.%s.amazonaws.com/%s",
                awsS3Config.getBucketName(),
                awsS3Config.getRegion(),
                key);
    }
    
    /**
     * Extract S3 key from full URL
     */
    private String extractKeyFromUrl(String url) {
        // Extract key from URL format: https://bucket-name.s3.region.amazonaws.com/key
        String[] parts = url.split(awsS3Config.getBucketName() + ".s3." + awsS3Config.getRegion() + ".amazonaws.com/");
        if (parts.length > 1) {
            return parts[1];
        }
        throw new IllegalArgumentException("Invalid S3 URL format: " + url);
    }

}
