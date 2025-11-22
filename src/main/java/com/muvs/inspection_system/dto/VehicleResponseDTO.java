package com.muvs.inspection_system.dto;

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
public class VehicleResponseDTO {
    
    private UUID id;
    private String plateNumber;
    private String model;
    private String manufacturer;
    private Integer year;
    private String status;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
