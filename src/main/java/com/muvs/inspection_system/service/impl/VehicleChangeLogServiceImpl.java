package com.muvs.inspection_system.service.impl;

import com.muvs.inspection_system.dto.VehicleChangeLogResponseDTO;
import com.muvs.inspection_system.entity.VehicleChangeLog;
import com.muvs.inspection_system.repository.VehicleChangeLogRepository;
import com.muvs.inspection_system.service.VehicleChangeLogService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class VehicleChangeLogServiceImpl implements VehicleChangeLogService {

    private final VehicleChangeLogRepository vehicleChangeLogRepository;

    @Override
    public List<VehicleChangeLogResponseDTO> getRecentActivities() {
        return vehicleChangeLogRepository.findTop10ByOrderByTimestampDesc()
                .stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }

    @Override
    public List<VehicleChangeLogResponseDTO> getAllChangeLogs() {
        return vehicleChangeLogRepository.findAll()
                .stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }

    @Override
    public List<VehicleChangeLogResponseDTO> getFilteredChangeLogs(
            String staffName,
            String changeType,
            LocalDateTime startDate,
            LocalDateTime endDate) {
        return vehicleChangeLogRepository.findByFilters(staffName, changeType, startDate, endDate)
                .stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }

    private VehicleChangeLogResponseDTO toDto(VehicleChangeLog log) {
        return VehicleChangeLogResponseDTO.builder()
                .id(log.getId())
                .checklistId(log.getChecklist().getId())
                .changeType(log.getChangeType())
                .oldVehiclePlate(log.getOldVehiclePlate())
                .newVehiclePlate(log.getNewVehiclePlate())
                .reason(log.getReason())
                .timestamp(log.getTimestamp())
                .staffName(log.getStaffName())
                .build();
    }
}
