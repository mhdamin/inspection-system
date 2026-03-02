package com.muvs.inspection_system.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class InspectionSummaryDTO {
    private Long totalInspections;
    private Long preRentalInspections;
    private Long postRentalInspections;
    private Long periodicInspections;
    private Long totalDefects;
    private Long unresolvedDefects;
    private Double averageDefectsPerInspection;
}
