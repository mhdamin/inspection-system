package com.muvs.inspection_system.service.impl;

import com.muvs.inspection_system.dto.*;
import com.muvs.inspection_system.entity.Checklist;
import com.muvs.inspection_system.entity.Defect;
import com.muvs.inspection_system.entity.Vehicle;
import com.muvs.inspection_system.repository.ChecklistRepository;
import com.muvs.inspection_system.repository.DefectRepository;
import com.muvs.inspection_system.repository.VehicleRepository;
import com.muvs.inspection_system.service.ReportingService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class ReportingServiceImpl implements ReportingService {

    private final ChecklistRepository checklistRepository;
    private final DefectRepository defectRepository;
    private final VehicleRepository vehicleRepository;

    @Override
    public InspectionSummaryDTO getInspectionSummary(LocalDate startDate, LocalDate endDate) {
        log.info("Generating inspection summary for period: {} to {}", startDate, endDate);

        LocalDateTime startDateTime = startDate.atStartOfDay();
        LocalDateTime endDateTime = endDate.atTime(23, 59, 59);

        // Get all checklists in date range
        List<Checklist> checklists = checklistRepository.findAll().stream()
                .filter(c -> !c.getCreatedAt().isBefore(startDateTime) && !c.getCreatedAt().isAfter(endDateTime))
                .collect(Collectors.toList());

        long totalInspections = checklists.size();
        long preRental = checklists.stream().filter(c -> "Pre-Rental".equals(c.getRentalType())).count();
        long postRental = checklists.stream().filter(c -> "Post-Rental".equals(c.getRentalType())).count();
        long periodic = checklists.stream().filter(c -> "Periodic".equals(c.getRentalType())).count();

        // Get all defects in date range
        List<Defect> allDefects = defectRepository.findAll().stream()
                .filter(d -> !d.getCreatedAt().isBefore(startDateTime) && !d.getCreatedAt().isAfter(endDateTime))
                .collect(Collectors.toList());

        long totalDefects = allDefects.size();
        // Note: Status field not yet implemented in Defect entity, so all defects are considered unresolved
        long unresolvedDefects = totalDefects;

        double averageDefects = totalInspections > 0 ? (double) totalDefects / totalInspections : 0.0;

        return InspectionSummaryDTO.builder()
                .totalInspections(totalInspections)
                .preRentalInspections(preRental)
                .postRentalInspections(postRental)
                .periodicInspections(periodic)
                .totalDefects(totalDefects)
                .unresolvedDefects(unresolvedDefects)
                .averageDefectsPerInspection(Math.round(averageDefects * 100.0) / 100.0)
                .build();
    }

    @Override
    public List<InspectionTrendDTO> getInspectionTrends(LocalDate startDate, LocalDate endDate) {
        log.info("Generating inspection trends for period: {} to {}", startDate, endDate);

        LocalDateTime startDateTime = startDate.atStartOfDay();
        LocalDateTime endDateTime = endDate.atTime(23, 59, 59);

        // Get all checklists in date range
        List<Checklist> checklists = checklistRepository.findAll().stream()
                .filter(c -> !c.getCreatedAt().isBefore(startDateTime) && !c.getCreatedAt().isAfter(endDateTime))
                .collect(Collectors.toList());

        // Get all defects in date range
        List<Defect> defects = defectRepository.findAll().stream()
                .filter(d -> !d.getCreatedAt().isBefore(startDateTime) && !d.getCreatedAt().isAfter(endDateTime))
                .collect(Collectors.toList());

        // Group by date
        Map<LocalDate, Long> checklistsByDate = checklists.stream()
                .collect(Collectors.groupingBy(
                        c -> c.getCreatedAt().toLocalDate(),
                        Collectors.counting()
                ));

        Map<LocalDate, Long> defectsByDate = defects.stream()
                .collect(Collectors.groupingBy(
                        d -> d.getCreatedAt().toLocalDate(),
                        Collectors.counting()
                ));

        // Create trend data for each date in range
        List<InspectionTrendDTO> trends = new ArrayList<>();
        LocalDate currentDate = startDate;
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");

        while (!currentDate.isAfter(endDate)) {
            long inspectionCount = checklistsByDate.getOrDefault(currentDate, 0L);
            long defectCount = defectsByDate.getOrDefault(currentDate, 0L);

            trends.add(InspectionTrendDTO.builder()
                    .date(currentDate.format(formatter))
                    .inspectionCount(inspectionCount)
                    .defectCount(defectCount)
                    .build());

            currentDate = currentDate.plusDays(1);
        }

        return trends;
    }

    @Override
    public List<DefectBreakdownDTO> getDefectBreakdown(LocalDate startDate, LocalDate endDate) {
        log.info("Generating defect breakdown for period: {} to {}", startDate, endDate);

        LocalDateTime startDateTime = startDate.atStartOfDay();
        LocalDateTime endDateTime = endDate.atTime(23, 59, 59);

        // Get all defects in date range
        List<Defect> defects = defectRepository.findAll().stream()
                .filter(d -> !d.getCreatedAt().isBefore(startDateTime) && !d.getCreatedAt().isAfter(endDateTime))
                .collect(Collectors.toList());

        long totalDefects = defects.size();

        // Count by type
        Map<String, Long> countsByType = defects.stream()
                .collect(Collectors.groupingBy(
                        d -> d.getDefectType().toString(),
                        Collectors.counting()
                ));

        // Calculate percentages and build DTOs
        return countsByType.entrySet().stream()
                .map(entry -> {
                    double percentage = totalDefects > 0
                            ? (double) entry.getValue() / totalDefects * 100.0
                            : 0.0;

                    return DefectBreakdownDTO.builder()
                            .defectType(entry.getKey())
                            .count(entry.getValue())
                            .percentage(Math.round(percentage * 10.0) / 10.0)
                            .build();
                })
                .sorted(Comparator.comparing(DefectBreakdownDTO::getCount).reversed())
                .collect(Collectors.toList());
    }

    @Override
    public List<VehicleStatusBreakdownDTO> getVehicleStatusTrends(LocalDate startDate, LocalDate endDate) {
        log.info("Generating vehicle status trends for period: {} to {}", startDate, endDate);

        // Since vehicle status changes over time are not tracked in current schema,
        // we'll provide a snapshot for each day based on current status
        // This is a simplified version - in production, you'd want a VehicleStatusHistory table

        List<Vehicle> allVehicles = vehicleRepository.findAll();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");

        List<VehicleStatusBreakdownDTO> trends = new ArrayList<>();
        LocalDate currentDate = startDate;

        // Count current status
        Map<String, Long> currentCounts = allVehicles.stream()
                .collect(Collectors.groupingBy(
                        v -> v.getStatus() != null ? v.getStatus().toString() : "AVAILABLE",
                        Collectors.counting()
                ));

        while (!currentDate.isAfter(endDate)) {
            // For now, use same counts for each day
            // In production, query historical status data
            trends.add(VehicleStatusBreakdownDTO.builder()
                    .date(currentDate.format(formatter))
                    .availableCount(currentCounts.getOrDefault("AVAILABLE", 0L))
                    .rentedCount(currentCounts.getOrDefault("RENTED", 0L))
                    .maintenanceCount(currentCounts.getOrDefault("MAINTENANCE", 0L))
                    .build());

            currentDate = currentDate.plusDays(1);
        }

        return trends;
    }
}
