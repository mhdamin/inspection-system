package com.muvs.inspection_system.service.impl;

import com.muvs.inspection_system.dto.ChecklistRequestDTO;
import com.muvs.inspection_system.dto.ChecklistResponseDTO;
import com.muvs.inspection_system.dto.VehicleSummaryDTO;
import com.muvs.inspection_system.entity.Checklist;
import com.muvs.inspection_system.entity.Vehicle;
import com.muvs.inspection_system.exception.ResourceNotFoundException;
import com.muvs.inspection_system.repository.ChecklistRepository;
import com.muvs.inspection_system.repository.ChecklistSpecification;
import com.muvs.inspection_system.repository.VehicleRepository;
import com.muvs.inspection_system.service.ChecklistService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class ChecklistServiceImpl implements ChecklistService {
    
    private final ChecklistRepository checklistRepository;
    private final VehicleRepository vehicleRepository;
    private final com.fasterxml.jackson.databind.ObjectMapper objectMapper;
    private final com.muvs.inspection_system.fleet.FleetService fleetService;
    
    @Override
    @Transactional
    public UUID createChecklist(ChecklistRequestDTO dto) {
        log.info("Creating checklist with number: {}", dto.getChecklistNumber());
        
        // Validate vehicle exists
        Vehicle vehicle = vehicleRepository.findById(dto.getVehicleId())
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Vehicle not found with ID: " + dto.getVehicleId()));
        
        // Check for duplicate checklist number
        if (checklistRepository.existsByChecklistNumber(dto.getChecklistNumber())) {
            Checklist previous = checklistRepository.findByChecklistNumber(dto.getChecklistNumber()).orElseThrow();
            if (previous.getStaffName().equals(dto.getStaffName()) && previous.getVehicle().getId().equals(dto.getVehicleId())
                    && (previous.getCompletedAt() != null) == dto.isCompleted()
                    && java.util.Objects.equals(previous.getRentalId(), dto.getRentalId())
                    && previous.getRentalType().equals(dto.getRentalType())
                    && previous.getCustomerName().equals(dto.getCustomerName())
                    && java.util.Objects.equals(readEvidence(previous.getEvidenceJson()), dto.getEvidence())) return previous.getId();
            throw new IllegalArgumentException(
                    "Checklist with number " + dto.getChecklistNumber() + " already exists");
        }
        
        Checklist checklist = Checklist.builder()
                .checklistNumber(dto.getChecklistNumber())
                .rentalStartDate(dto.getRentalStartDate())
                .rentalEndDate(dto.getRentalEndDate())
                .customerName(dto.getCustomerName())
                .customerPhone(dto.getCustomerPhone())
                .staffName(dto.getStaffName())
                .rentalType(dto.getRentalType())
                .vehicle(vehicle)
                .build();
        
        applyEvidence(checklist, dto);
        Checklist savedChecklist = checklistRepository.save(checklist);
        log.info("Checklist created successfully with ID: {}", savedChecklist.getId());
        
        return savedChecklist.getId();
    }
    
    @Override
    @Transactional(readOnly = true)
    public ChecklistResponseDTO getChecklist(UUID id) {
        log.info("Fetching checklist with ID: {}", id);
        
        Checklist checklist = checklistRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Checklist not found with ID: " + id));
        
        return toDto(checklist);
    }
    
    @Override
    @Transactional(readOnly = true)
    public List<ChecklistResponseDTO> getAllChecklists() {
        log.info("Fetching all checklists");
        
        return checklistRepository.findAll()
                .stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }
    
    @Override
    @Transactional
    public ChecklistResponseDTO updateChecklist(UUID id, ChecklistRequestDTO dto) {
        log.info("Updating checklist with ID: {}", id);
        
        Checklist checklist = checklistRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Checklist not found with ID: " + id));
        
        // Validate vehicle exists
        Vehicle vehicle = vehicleRepository.findById(dto.getVehicleId())
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Vehicle not found with ID: " + dto.getVehicleId()));
        
        if (checklist.getCompletedAt() != null) throw new IllegalStateException("Completed inspections cannot be edited. Create a new inspection.");
        // Check if checklist number is being changed and if it already exists
        if (!checklist.getChecklistNumber().equals(dto.getChecklistNumber())
                && checklistRepository.existsByChecklistNumber(dto.getChecklistNumber())) {
            throw new IllegalArgumentException(
                    "Checklist with number " + dto.getChecklistNumber() + " already exists");
        }
        
        checklist.setChecklistNumber(dto.getChecklistNumber());
        checklist.setRentalStartDate(dto.getRentalStartDate());
        checklist.setRentalEndDate(dto.getRentalEndDate());
        checklist.setCustomerName(dto.getCustomerName());
        checklist.setCustomerPhone(dto.getCustomerPhone());
        checklist.setStaffName(dto.getStaffName());
        checklist.setRentalType(dto.getRentalType());
        checklist.setVehicle(vehicle);
        
        applyEvidence(checklist, dto);
        Checklist updatedChecklist = checklistRepository.save(checklist);
        log.info("Checklist updated successfully with ID: {}", updatedChecklist.getId());
        
        return toDto(updatedChecklist);
    }
    
    @Override
    @Transactional
    public void deleteChecklist(UUID id) {
        log.info("Deleting checklist with ID: {}", id);
        
        if (!checklistRepository.existsById(id)) {
            throw new ResourceNotFoundException("Checklist not found with ID: " + id);
        }
        
        Checklist checklist = checklistRepository.findById(id).orElseThrow();
        if (checklist.getCompletedAt() != null) throw new IllegalStateException("Completed inspections are retained as evidence and cannot be deleted.");
        checklistRepository.deleteById(id);
        log.info("Checklist deleted successfully with ID: {}", id);
    }
    
    @Override
    @Transactional(readOnly = true)
    public ChecklistResponseDTO findByChecklistNumber(String checklistNumber) {
        log.info("Fetching checklist with number: {}", checklistNumber);
        
        Checklist checklist = checklistRepository.findByChecklistNumber(checklistNumber)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Checklist not found with number: " + checklistNumber));
        
        return toDto(checklist);
    }
    
    @Override
    @Transactional(readOnly = true)
    public List<ChecklistResponseDTO> searchChecklists(
            String checklistNumber,
            String plateNumber,
            String customerName,
            LocalDate startDate,
            LocalDate endDate) {
        
        log.info("Searching checklists with filters - checklistNumber: {}, plateNumber: {}, " +
                        "customerName: {}, startDate: {}, endDate: {}",
                checklistNumber, plateNumber, customerName, startDate, endDate);
        
        Specification<Checklist> specification = ChecklistSpecification.withFilters(
                checklistNumber, plateNumber, customerName, startDate, endDate);
        
        return checklistRepository.findAll(specification)
                .stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }
    
    private ChecklistResponseDTO toDto(Checklist checklist) {
        return ChecklistResponseDTO.builder()
                .rentalId(checklist.getRentalId())
                .completed(checklist.getCompletedAt() != null)
                .completedAt(checklist.getCompletedAt())
                .evidence(readEvidence(checklist.getEvidenceJson()))
                .id(checklist.getId())
                .checklistNumber(checklist.getChecklistNumber())
                .rentalStartDate(checklist.getRentalStartDate())
                .rentalEndDate(checklist.getRentalEndDate())
                .customerName(checklist.getCustomerName())
                .customerPhone(checklist.getCustomerPhone())
                .staffName(checklist.getStaffName())
                .rentalType(checklist.getRentalType())
                .vehicle(toVehicleSummary(checklist.getVehicle()))
                .createdAt(checklist.getCreatedAt())
                .build();
    }
    
    private void applyEvidence(Checklist checklist, ChecklistRequestDTO dto) {
        if (dto.getRentalId() != null) {
            var rental = fleetService.getRental(dto.getRentalId());
            if (!dto.getVehicleId().toString().equals(String.valueOf(rental.get("vehicleId"))))
                throw new IllegalArgumentException("Inspection vehicle must match the rental vehicle");
            if (!dto.getCustomerName().trim().equalsIgnoreCase(String.valueOf(rental.get("customerName")).trim()))
                throw new IllegalArgumentException("Inspection customer must match the rental customer");
        }
        var evidence = dto.getEvidence();
        if (dto.isCompleted()) {
            if (evidence == null || !evidence.isAcknowledged() || evidence.getAcknowledgedBy() == null
                    || !dto.getCustomerName().trim().equalsIgnoreCase(evidence.getAcknowledgedBy().trim()))
                throw new IllegalArgumentException("Customer acknowledgement is required to complete an inspection");
            if (evidence.getExterior().size() != 20 || evidence.getExterior().stream().map(p -> p.getId()).distinct().count() != 20
                    || evidence.getExterior().stream().anyMatch(p -> "Not Inspected".equals(p.getStatus())))
                throw new IllegalArgumentException("Inspect all exterior points before completing");
            if (!evidence.getInterior().keySet().equals(java.util.Set.of("dashboard", "seats", "carpets", "windows", "electronics", "safety"))
                    || evidence.getInterior().values().stream().anyMatch(v -> v == null || v.isBlank()))
                throw new IllegalArgumentException("Complete all interior observations");
            if (!evidence.getTyres().keySet().equals(java.util.Set.of("frontLeft", "frontRight", "rearLeft", "rearRight"))
                    || evidence.getTyres().values().stream().anyMatch(v -> !java.util.Set.of("Normal", "Abnormal", "N/A").contains(v)))
                throw new IllegalArgumentException("Inspect all four tyres");
            if (evidence.getExterior().stream().anyMatch(p -> "Abnormal".equals(p.getStatus()) && (p.getNotes() == null || p.getNotes().isBlank())))
                throw new IllegalArgumentException("Describe each abnormal exterior condition");
            checklist.setCompletedAt(java.time.LocalDateTime.now());
        }
        checklist.setRentalId(dto.getRentalId());
        try {
            checklist.setEvidenceJson(evidence == null ? null : objectMapper.writeValueAsString(evidence));
        } catch (com.fasterxml.jackson.core.JsonProcessingException ex) {
            throw new IllegalArgumentException("Unable to save inspection evidence", ex);
        }
    }

    private com.muvs.inspection_system.dto.InspectionEvidenceDTO readEvidence(String json) {
        if (json == null) return null;
        try {
            return objectMapper.readValue(json, com.muvs.inspection_system.dto.InspectionEvidenceDTO.class);
        } catch (com.fasterxml.jackson.core.JsonProcessingException ex) {
            throw new IllegalStateException("Inspection evidence could not be read", ex);
        }
    }

    private VehicleSummaryDTO toVehicleSummary(Vehicle vehicle) {
        return VehicleSummaryDTO.builder()
                .id(vehicle.getId())
                .plateNumber(vehicle.getPlateNumber())
                .model(vehicle.getModel())
                .manufacturer(vehicle.getManufacturer())
                .year(vehicle.getYear())
                .build();
    }
}
