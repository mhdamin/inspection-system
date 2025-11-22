package com.muvs.inspection_system.dto;

import com.muvs.inspection_system.enums.SubChecklistType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SubChecklistRequestDTO {
    
    @NotNull(message = "Sub-checklist type is required")
    private SubChecklistType type;
    
    private String subChecklistNumber; // Optional; will be auto-generated if not provided
    
    private LocalDateTime timestamp; // Optional; will use current time if not provided
    
    @NotBlank(message = "Staff name is required")
    private String staffName;
    
    private String remarks; // Optional
    
    private UUID vehicleId; // Optional; for temporary/permanent replacement vehicles
    
    @NotNull(message = "Checklist ID is required")
    private UUID checklistId;
}