package com.muvs.inspection_system.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class VehicleStatusBreakdownDTO {
    private String date;              // Format: "YYYY-MM-DD"
    private Long availableCount;
    private Long rentedCount;
    private Long maintenanceCount;
}
