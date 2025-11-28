package com.muvs.inspection_system.dto;

import com.muvs.inspection_system.enums.ChangeType;
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
public class VehicleChangeLogResponseDTO {
    private UUID id;
    private UUID checklistId;
    private ChangeType changeType;
    private String oldVehiclePlate;
    private String newVehiclePlate;
    private String reason;
    private LocalDateTime timestamp;
    private String staffName;
}
