package com.muvs.inspection_system.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DefectImageResponseDTO {
    
    private Long imageId;
    private Long defectId;
    private String imageUrl;
    private String fileName;
    private Long fileSize;
    private Long uploadedBy;
    private LocalDateTime uploadedAt;
}