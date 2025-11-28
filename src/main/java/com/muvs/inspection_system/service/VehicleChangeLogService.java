package com.muvs.inspection_system.service;

import com.muvs.inspection_system.dto.VehicleChangeLogResponseDTO;

import java.time.LocalDateTime;
import java.util.List;

public interface VehicleChangeLogService {
    List<VehicleChangeLogResponseDTO> getRecentActivities();
    List<VehicleChangeLogResponseDTO> getAllChangeLogs();
    List<VehicleChangeLogResponseDTO> getFilteredChangeLogs(
            String staffName,
            String changeType,
            LocalDateTime startDate,
            LocalDateTime endDate
    );
}
