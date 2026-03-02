package com.muvs.inspection_system.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DefectBreakdownDTO {
    private String defectType;      // SCRATCH, DENT, CRACK
    private Long count;
    private Double percentage;
}
