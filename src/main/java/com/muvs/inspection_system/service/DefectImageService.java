package com.muvs.inspection_system.service;

import com.muvs.inspection_system.dto.DefectImageResponseDTO;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface DefectImageService {
    
    DefectImageResponseDTO uploadImage(Long defectId, MultipartFile file, Long uploadedBy);
    
    List<DefectImageResponseDTO> getImagesByDefect(Long defectId);
    
    void deleteImage(Long imageId);
}