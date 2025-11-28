package com.muvs.inspection_system.service.impl;

import com.muvs.inspection_system.dto.*;
import com.muvs.inspection_system.entity.Checklist;
import com.muvs.inspection_system.entity.SubChecklist;
import com.muvs.inspection_system.entity.Vehicle;
import com.muvs.inspection_system.entity.VehicleChangeLog;
import com.muvs.inspection_system.enums.ChangeType;
import com.muvs.inspection_system.enums.SubChecklistType;
import com.muvs.inspection_system.exception.ResourceNotFoundException;
import com.muvs.inspection_system.repository.ChecklistRepository;
import com.muvs.inspection_system.repository.SubChecklistRepository;
import com.muvs.inspection_system.repository.VehicleChangeLogRepository;
import com.muvs.inspection_system.repository.VehicleRepository;
import com.muvs.inspection_system.service.SubChecklistService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class SubChecklistServiceImpl implements SubChecklistService {

    private final SubChecklistRepository subChecklistRepository;
    private final ChecklistRepository checklistRepository;
    private final VehicleRepository vehicleRepository;
    private final VehicleChangeLogRepository vehicleChangeLogRepository;
    
    @Override
    @Transactional
    public UUID createSubChecklist(SubChecklistRequestDTO dto) {
        log.info("Creating sub-checklist for checklist ID: {}", dto.getChecklistId());
        
        // Validate parent checklist exists
        Checklist checklist = checklistRepository.findById(dto.getChecklistId())
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Checklist not found with ID: " + dto.getChecklistId()));
        
        // Validate vehicle exists if vehicleId is provided
        if (dto.getVehicleId() != null) {
            vehicleRepository.findById(dto.getVehicleId())
                    .orElseThrow(() -> new ResourceNotFoundException(
                            "Vehicle not found with ID: " + dto.getVehicleId()));
        }
        
        // Generate sub-checklist number if not provided
        String subChecklistNumber = dto.getSubChecklistNumber();
        if (subChecklistNumber == null || subChecklistNumber.trim().isEmpty()) {
            subChecklistNumber = generateSubChecklistNumber(checklist.getChecklistNumber());
        } else {
            // Check for duplicate if provided
            if (subChecklistRepository.existsBySubChecklistNumber(subChecklistNumber)) {
                throw new IllegalArgumentException(
                        "Sub-checklist with number " + subChecklistNumber + " already exists");
            }
        }
        
        // Use provided timestamp or current time
        LocalDateTime timestamp = dto.getTimestamp() != null ? dto.getTimestamp() : LocalDateTime.now();
        
        SubChecklist subChecklist = SubChecklist.builder()
                .type(dto.getType())
                .subChecklistNumber(subChecklistNumber)
                .timestamp(timestamp)
                .staffName(dto.getStaffName())
                .remarks(dto.getRemarks())
                .vehicleId(dto.getVehicleId())
                .checklist(checklist)
                .build();
        
        try {
            SubChecklist savedSubChecklist = subChecklistRepository.save(subChecklist);
            log.info("Sub-checklist created successfully with ID: {}", savedSubChecklist.getId());

            // Create VehicleChangeLog when type is TEMPORARY or PERMANENT
            if (dto.getType() == SubChecklistType.TEMPORARY || dto.getType() == SubChecklistType.PERMANENT) {
                log.info("Creating VehicleChangeLog for sub-checklist type: {}", dto.getType());
                createVehicleChangeLog(savedSubChecklist, checklist);
            }

            return savedSubChecklist.getId();
        } catch (DataIntegrityViolationException e) {
            log.error("Duplicate sub-checklist number detected: {}", subChecklistNumber);
            throw new IllegalArgumentException(
                    "Sub-checklist with number " + subChecklistNumber + " already exists");
        }
    }
    
    @Override
    @Transactional(readOnly = true)
    public SubChecklistResponseDTO getSubChecklist(UUID id) {
        log.info("Fetching sub-checklist with ID: {}", id);
        
        SubChecklist subChecklist = subChecklistRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Sub-checklist not found with ID: " + id));
        
        return toDto(subChecklist);
    }
    
    @Override
    @Transactional(readOnly = true)
    public List<SubChecklistResponseDTO> getSubChecklistsByChecklist(UUID checklistId) {
        log.info("Fetching sub-checklists for checklist ID: {}", checklistId);
        
        // Verify checklist exists
        if (!checklistRepository.existsById(checklistId)) {
            throw new ResourceNotFoundException("Checklist not found with ID: " + checklistId);
        }
        
        return subChecklistRepository.findByChecklistIdOrderByTimestampAsc(checklistId)
                .stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }
    
    @Override
    @Transactional(readOnly = true)
    public List<SubChecklistResponseDTO> getAllSubChecklists() {
        log.info("Fetching all sub-checklists");
        
        return subChecklistRepository.findAll()
                .stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }
    
    @Override
    @Transactional
    public SubChecklistResponseDTO updateSubChecklist(UUID id, SubChecklistRequestDTO dto) {
        log.info("Updating sub-checklist with ID: {}", id);
        
        SubChecklist subChecklist = subChecklistRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Sub-checklist not found with ID: " + id));
        
        // Validate parent checklist exists
        Checklist checklist = checklistRepository.findById(dto.getChecklistId())
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Checklist not found with ID: " + dto.getChecklistId()));
        
        // Validate vehicle exists if vehicleId is provided
        if (dto.getVehicleId() != null) {
            vehicleRepository.findById(dto.getVehicleId())
                    .orElseThrow(() -> new ResourceNotFoundException(
                            "Vehicle not found with ID: " + dto.getVehicleId()));
        }
        
        // Check if sub-checklist number is being changed and if it already exists
        if (dto.getSubChecklistNumber() != null 
                && !subChecklist.getSubChecklistNumber().equals(dto.getSubChecklistNumber())
                && subChecklistRepository.existsBySubChecklistNumber(dto.getSubChecklistNumber())) {
            throw new IllegalArgumentException(
                    "Sub-checklist with number " + dto.getSubChecklistNumber() + " already exists");
        }
        
        // Update fields
        subChecklist.setType(dto.getType());
        if (dto.getSubChecklistNumber() != null && !dto.getSubChecklistNumber().trim().isEmpty()) {
            subChecklist.setSubChecklistNumber(dto.getSubChecklistNumber());
        }
        if (dto.getTimestamp() != null) {
            subChecklist.setTimestamp(dto.getTimestamp());
        }
        subChecklist.setStaffName(dto.getStaffName());
        subChecklist.setRemarks(dto.getRemarks());
        subChecklist.setVehicleId(dto.getVehicleId());
        subChecklist.setChecklist(checklist);
        
        SubChecklist updatedSubChecklist = subChecklistRepository.save(subChecklist);
        log.info("Sub-checklist updated successfully with ID: {}", updatedSubChecklist.getId());
        
        return toDto(updatedSubChecklist);
    }
    
    @Override
    @Transactional
    public void deleteSubChecklist(UUID id) {
        log.info("Deleting sub-checklist with ID: {}", id);

        if (!subChecklistRepository.existsById(id)) {
            throw new ResourceNotFoundException("Sub-checklist not found with ID: " + id);
        }

        // Note: VehicleChangeLog records should be preserved for audit history
        subChecklistRepository.deleteById(id);
        log.info("Sub-checklist deleted successfully with ID: {}", id);
    }

    /**
     * Create VehicleChangeLog entry when vehicle is changed (TEMPORARY or PERMANENT)
     * This tracks the vehicle change history for audit and reporting purposes.
     */
    private void createVehicleChangeLog(SubChecklist subChecklist, Checklist checklist) {
        // Get the original vehicle from the parent checklist
        Vehicle originalVehicle = checklist.getVehicle();
        String oldVehiclePlate = originalVehicle != null ? originalVehicle.getPlateNumber() : null;

        // Get the new vehicle from the sub-checklist
        String newVehiclePlate = null;
        if (subChecklist.getVehicleId() != null) {
            Vehicle newVehicle = vehicleRepository.findById(subChecklist.getVehicleId())
                    .orElse(null);
            newVehiclePlate = newVehicle != null ? newVehicle.getPlateNumber() : null;
        }

        // Map SubChecklistType to ChangeType (they have the same enum values)
        ChangeType changeType = ChangeType.valueOf(subChecklist.getType().name());

        // Use remarks as reason, or provide a default reason based on change type
        String reason = subChecklist.getRemarks();
        if (reason == null || reason.trim().isEmpty()) {
            reason = switch (changeType) {
                case TEMPORARY -> "Temporary vehicle replacement";
                case PERMANENT -> "Permanent vehicle change";
                case RETURN -> "Vehicle returned";
            };
        }

        // Create the VehicleChangeLog entry
        VehicleChangeLog changeLog = VehicleChangeLog.builder()
                .changeType(changeType)
                .oldVehiclePlate(oldVehiclePlate)
                .newVehiclePlate(newVehiclePlate)
                .reason(reason)
                .timestamp(subChecklist.getTimestamp())
                .staffName(subChecklist.getStaffName())
                .checklist(checklist)
                .build();

        vehicleChangeLogRepository.save(changeLog);
        log.info("VehicleChangeLog created: {} from {} to {} - {}",
                changeType, oldVehiclePlate, newVehiclePlate, reason);
    }

    /**
     * Generate a unique sub-checklist number based on parent checklist number
     * Format: <parentChecklistNumber>-SUB-<timestamp>
     */
    private String generateSubChecklistNumber(String parentChecklistNumber) {
        String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss"));
        String generated = parentChecklistNumber + "-SUB-" + timestamp;
        
        // Ensure uniqueness (handle edge case of same-second creation)
        int counter = 1;
        String candidate = generated;
        while (subChecklistRepository.existsBySubChecklistNumber(candidate)) {
            candidate = generated + "-" + counter;
            counter++;
        }
        
        return candidate;
    }
    
    /**
     * Convert SubChecklist entity to DTO
     */
    private SubChecklistResponseDTO toDto(SubChecklist subChecklist) {
        return SubChecklistResponseDTO.builder()
                .id(subChecklist.getId())
                .type(subChecklist.getType())
                .subChecklistNumber(subChecklist.getSubChecklistNumber())
                .timestamp(subChecklist.getTimestamp())
                .staffName(subChecklist.getStaffName())
                .remarks(subChecklist.getRemarks())
                .vehicle(toVehicleSummary(subChecklist.getVehicleId()))
                .checklist(toChecklistSummary(subChecklist.getChecklist()))
                .createdAt(subChecklist.getCreatedAt())
                .updatedAt(subChecklist.getUpdatedAt())
                .build();
    }
    
    /**
     * Convert Vehicle ID to VehicleSummaryDTO
     * Returns null if vehicleId is null
     */
    private VehicleSummaryDTO toVehicleSummary(UUID vehicleId) {
        if (vehicleId == null) {
            return null;
        }
        
        return vehicleRepository.findById(vehicleId)
                .map(vehicle -> VehicleSummaryDTO.builder()
                        .id(vehicle.getId())
                        .plateNumber(vehicle.getPlateNumber())
                        .model(vehicle.getModel())
                        .build())
                .orElse(null);
    }
    
    /**
     * Convert Checklist entity to ChecklistSummaryDTO
     */
    private ChecklistSummaryDTO toChecklistSummary(Checklist checklist) {
        return ChecklistSummaryDTO.builder()
                .id(checklist.getId())
                .checklistNumber(checklist.getChecklistNumber())
                .build();
    }
}