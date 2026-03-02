package com.muvs.inspection_system.controller;

import com.muvs.inspection_system.dto.*;
import com.muvs.inspection_system.service.ReportingService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@Slf4j
@RestController
@RequestMapping("/api/reports")
@RequiredArgsConstructor
public class ReportController {

    private final ReportingService reportingService;

    /**
     * Get inspection summary statistics
     * GET /api/reports/inspection-summary?startDate=2025-01-01&endDate=2025-01-31
     */
    @GetMapping("/inspection-summary")
    public ResponseEntity<InspectionSummaryDTO> getInspectionSummary(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {

        // Default to last 30 days if not provided
        if (endDate == null) {
            endDate = LocalDate.now();
        }
        if (startDate == null) {
            startDate = endDate.minusDays(30);
        }

        log.info("GET /api/reports/inspection-summary - startDate: {}, endDate: {}", startDate, endDate);

        InspectionSummaryDTO summary = reportingService.getInspectionSummary(startDate, endDate);
        return ResponseEntity.ok(summary);
    }

    /**
     * Get inspection trends (daily data points)
     * GET /api/reports/inspection-trends?startDate=2025-01-01&endDate=2025-01-31
     */
    @GetMapping("/inspection-trends")
    public ResponseEntity<List<InspectionTrendDTO>> getInspectionTrends(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {

        // Default to last 30 days if not provided
        if (endDate == null) {
            endDate = LocalDate.now();
        }
        if (startDate == null) {
            startDate = endDate.minusDays(30);
        }

        log.info("GET /api/reports/inspection-trends - startDate: {}, endDate: {}", startDate, endDate);

        List<InspectionTrendDTO> trends = reportingService.getInspectionTrends(startDate, endDate);
        return ResponseEntity.ok(trends);
    }

    /**
     * Get defect breakdown by type
     * GET /api/reports/defect-breakdown?startDate=2025-01-01&endDate=2025-01-31
     */
    @GetMapping("/defect-breakdown")
    public ResponseEntity<List<DefectBreakdownDTO>> getDefectBreakdown(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {

        // Default to last 30 days if not provided
        if (endDate == null) {
            endDate = LocalDate.now();
        }
        if (startDate == null) {
            startDate = endDate.minusDays(30);
        }

        log.info("GET /api/reports/defect-breakdown - startDate: {}, endDate: {}", startDate, endDate);

        List<DefectBreakdownDTO> breakdown = reportingService.getDefectBreakdown(startDate, endDate);
        return ResponseEntity.ok(breakdown);
    }

    /**
     * Get vehicle status trends over time
     * GET /api/reports/vehicle-status-trends?startDate=2025-01-01&endDate=2025-01-31
     */
    @GetMapping("/vehicle-status-trends")
    public ResponseEntity<List<VehicleStatusBreakdownDTO>> getVehicleStatusTrends(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {

        // Default to last 30 days if not provided
        if (endDate == null) {
            endDate = LocalDate.now();
        }
        if (startDate == null) {
            startDate = endDate.minusDays(30);
        }

        log.info("GET /api/reports/vehicle-status-trends - startDate: {}, endDate: {}", startDate, endDate);

        List<VehicleStatusBreakdownDTO> trends = reportingService.getVehicleStatusTrends(startDate, endDate);
        return ResponseEntity.ok(trends);
    }
}
