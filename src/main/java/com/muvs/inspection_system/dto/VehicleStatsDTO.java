package com.muvs.inspection_system.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class VehicleStatsDTO {
    private long totalVehicles;
    private long availableVehicles;
    private long rentedVehicles;
    private long maintenanceVehicles;
}
