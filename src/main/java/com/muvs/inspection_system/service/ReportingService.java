package com.muvs.inspection_system.service;

import com.muvs.inspection_system.dto.*;

import java.time.LocalDate;
import java.util.List;

public interface ReportingService {

    /**
     * Get inspection summary statistics for a date range
     * @param startDate Start date (inclusive)
     * @param endDate End date (inclusive)
     * @return Inspection summary with counts and averages
     */
    InspectionSummaryDTO getInspectionSummary(LocalDate startDate, LocalDate endDate);

    /**
     * Get inspection trends over time (daily data points)
     * @param startDate Start date (inclusive)
     * @param endDate End date (inclusive)
     * @return List of daily inspection counts
     */
    List<InspectionTrendDTO> getInspectionTrends(LocalDate startDate, LocalDate endDate);

    /**
     * Get defect breakdown by type with percentages
     * @param startDate Start date (inclusive)
     * @param endDate End date (inclusive)
     * @return List of defect counts by type
     */
    List<DefectBreakdownDTO> getDefectBreakdown(LocalDate startDate, LocalDate endDate);

    /**
     * Get vehicle status distribution over time
     * @param startDate Start date (inclusive)
     * @param endDate End date (inclusive)
     * @return List of daily vehicle status counts
     */
    List<VehicleStatusBreakdownDTO> getVehicleStatusTrends(LocalDate startDate, LocalDate endDate);
}
