package com.muvs.inspection_system.dto;

import com.muvs.inspection_system.enums.DefectType;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DefectRequestDTO {
    
    private Long itemId;
    
    @NotNull(message = "Defect type is required")
    private DefectType defectType;
    
    @Size(max = 500, message = "Description cannot exceed 500 characters")
    private String description;
    
    private Integer diagramX;
    
    private Integer diagramY;
}