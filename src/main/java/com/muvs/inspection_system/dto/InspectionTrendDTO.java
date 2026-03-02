package com.muvs.inspection_system.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class InspectionTrendDTO {
    private String date;           // Format: "YYYY-MM-DD" or "Mon", "Tue", etc.
    private Long inspectionCount;
    private Long defectCount;
}
