package com.muvs.inspection_system.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ChecklistResponseDTO {
    
    private UUID id;
    private String checklistNumber;
    private LocalDate rentalStartDate;
    private LocalDate rentalEndDate;
    private String customerName;
    private String customerPhone;
    private String staffName;
    private String rentalType;
    private VehicleSummaryDTO vehicle;
    private LocalDateTime createdAt;
}