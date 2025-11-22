package com.muvs.inspection_system.dto;

import com.muvs.inspection_system.enums.SubChecklistType;
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
public class SubChecklistResponseDTO {
    
    private UUID id;
    private SubChecklistType type;
    private String subChecklistNumber;
    private LocalDateTime timestamp;
    private String staffName;
    private String remarks;
    private VehicleSummaryDTO vehicle; // Null if no vehicle associated
    private ChecklistSummaryDTO checklist;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}