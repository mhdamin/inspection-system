package com.muvs.inspection_system.controller;

import com.muvs.inspection_system.dto.VehicleChangeLogResponseDTO;
import com.muvs.inspection_system.service.VehicleChangeLogService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@RestController
@RequestMapping("/api/activities")
@RequiredArgsConstructor
public class VehicleChangeLogController {

    private final VehicleChangeLogService vehicleChangeLogService;

    @GetMapping
    public ResponseEntity<List<VehicleChangeLogResponseDTO>> getRecentActivities() {
        log.info("GET /api/activities - Fetching recent activities");
        List<VehicleChangeLogResponseDTO> activities = vehicleChangeLogService.getRecentActivities();
        return ResponseEntity.ok(activities);
    }

    @GetMapping("/all")
    public ResponseEntity<List<VehicleChangeLogResponseDTO>> getAllActivities() {
        log.info("GET /api/activities/all - Fetching all activities");
        List<VehicleChangeLogResponseDTO> activities = vehicleChangeLogService.getAllChangeLogs();
        return ResponseEntity.ok(activities);
    }

    @GetMapping("/filter")
    public ResponseEntity<List<VehicleChangeLogResponseDTO>> getFilteredActivities(
            @RequestParam(required = false) String staffName,
            @RequestParam(required = false) String changeType,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endDate) {
        log.info("GET /api/activities/filter - Filtering activities with staffName={}, changeType={}, startDate={}, endDate={}",
                staffName, changeType, startDate, endDate);
        List<VehicleChangeLogResponseDTO> activities = vehicleChangeLogService.getFilteredChangeLogs(
                staffName, changeType, startDate, endDate);
        return ResponseEntity.ok(activities);
    }
}
