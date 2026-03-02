package com.muvs.inspection_system.service.impl;

import com.muvs.inspection_system.dto.DefectImageResponseDTO;
import com.muvs.inspection_system.entity.Defect;
import com.muvs.inspection_system.entity.DefectImage;
import com.muvs.inspection_system.exception.ResourceNotFoundException;
import com.muvs.inspection_system.repository.DefectImageRepository;
import com.muvs.inspection_system.repository.DefectRepository;
import com.muvs.inspection_system.service.DefectImageService;
import com.muvs.inspection_system.service.ImageStorageService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
public class DefectImageServiceImpl implements DefectImageService {

    private final DefectImageRepository defectImageRepository;
    private final DefectRepository defectRepository;
    private final ImageStorageService imageStorageService;

    public DefectImageServiceImpl(
            DefectImageRepository defectImageRepository,
            DefectRepository defectRepository,
            @Autowired(required = false) ImageStorageService imageStorageService) {
        this.defectImageRepository = defectImageRepository;
        this.defectRepository = defectRepository;
        this.imageStorageService = imageStorageService;
    }
    
    @Override
    @Transactional
    public DefectImageResponseDTO uploadImage(Long defectId, MultipartFile file, Long uploadedBy) {
        log.info("Uploading image for defect ID: {}", defectId);

        ensureStorageConfigured();
        
        // Validate defect exists
        Defect defect = defectRepository.findById(defectId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Defect not found with ID: " + defectId));
        
        // Validate file
        if (file.isEmpty()) {
            throw new IllegalArgumentException("File is empty");
        }
        
        // Validate file type
        String contentType = file.getContentType();
        if (contentType == null || !contentType.startsWith("image/")) {
            throw new IllegalArgumentException("File must be an image");
        }
        
        try {
            // Upload to S3
            String folderPath = "defects/" + defectId;
            String imageUrl = imageStorageService.uploadFile(file, folderPath);
            
            // Save metadata to database
            DefectImage defectImage = DefectImage.builder()
                    .defect(defect)
                    .imageUrl(imageUrl)
                    .fileName(file.getOriginalFilename())
                    .fileSize(file.getSize())
                    .uploadedBy(uploadedBy)
                    .build();
            
            DefectImage savedImage = defectImageRepository.save(defectImage);
            log.info("Image uploaded successfully with ID: {}", savedImage.getImageId());
            
            return toDto(savedImage);
        } catch (IOException e) {
            log.error("Failed to upload image for defect ID: {}", defectId, e);
            throw new RuntimeException("Failed to upload image: " + e.getMessage(), e);
        }
    }
    
    @Override
    @Transactional(readOnly = true)
    public List<DefectImageResponseDTO> getImagesByDefect(Long defectId) {
        log.info("Fetching images for defect ID: {}", defectId);
        
        // Validate defect exists
        if (!defectRepository.existsById(defectId)) {
            throw new ResourceNotFoundException("Defect not found with ID: " + defectId);
        }
        
        return defectImageRepository.findByDefectIdOrderByUploadedAtAsc(defectId)
                .stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }
    
    @Override
    @Transactional
    public void deleteImage(Long imageId) {
        log.info("Deleting image with ID: {}", imageId);

        ensureStorageConfigured();
        
        DefectImage image = defectImageRepository.findById(imageId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Image not found with ID: " + imageId));
        
        try {
            // Delete from S3
            imageStorageService.deleteFile(image.getImageUrl());
            
            // Delete from database
            defectImageRepository.delete(image);
            log.info("Image deleted successfully with ID: {}", imageId);
        } catch (Exception e) {
            log.error("Failed to delete image with ID: {}", imageId, e);
            throw new RuntimeException("Failed to delete image: " + e.getMessage(), e);
        }
    }
    
    private DefectImageResponseDTO toDto(DefectImage image) {
        return DefectImageResponseDTO.builder()
                .imageId(image.getImageId())
                .defectId(image.getDefectId())
                .imageUrl(image.getImageUrl())
                .fileName(image.getFileName())
                .fileSize(image.getFileSize())
                .uploadedBy(image.getUploadedBy())
                .uploadedAt(image.getUploadedAt())
                .build();
    }

    private void ensureStorageConfigured() {
        if (imageStorageService == null) {
            throw new IllegalStateException("Image storage is not configured. Enable aws.s3.enabled=true to upload/delete images.");
        }
    }
}
