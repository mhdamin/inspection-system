package com.muvs.inspection_system.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ChecklistRequestDTO {
    
    @NotBlank(message = "Checklist number is required")
    private String checklistNumber;
    
    @NotNull(message = "Rental start date is required")
    private LocalDate rentalStartDate;
    
    private LocalDate rentalEndDate;
    
    @NotBlank(message = "Customer name is required")
    private String customerName;
    
    @NotBlank(message = "Customer phone is required")
    private String customerPhone;
    
    @NotBlank(message = "Staff name is required")
    private String staffName;
    
    @NotBlank(message = "Rental type is required")
    private String rentalType;
    
    @NotNull(message = "Vehicle ID is required")
    private UUID vehicleId;
}