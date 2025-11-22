package com.muvs.inspection_system.dto;

import com.muvs.inspection_system.enums.DefectType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DefectResponseDTO {

    private Long defectId;
    private UUID checklistId;
    private Long itemId;
    private DefectType defectType;
    private String description;
    private Integer diagramX;
    private Integer diagramY;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private List<DefectImageResponseDTO> images;
}