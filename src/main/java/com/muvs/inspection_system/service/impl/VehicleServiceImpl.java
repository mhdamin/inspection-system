package com.muvs.inspection_system.service.impl;

import com.muvs.inspection_system.dto.VehicleRequestDTO;
import com.muvs.inspection_system.dto.VehicleResponseDTO;
import com.muvs.inspection_system.dto.VehicleStatsDTO;
import com.muvs.inspection_system.entity.Vehicle;
import com.muvs.inspection_system.exception.ResourceNotFoundException;
import com.muvs.inspection_system.repository.VehicleRepository;
import com.muvs.inspection_system.service.VehicleService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class VehicleServiceImpl implements VehicleService {

    private final VehicleRepository vehicleRepository;

    @Override
    @Transactional
    public UUID createVehicle(VehicleRequestDTO dto) {
        log.info("Creating vehicle with plate number: {}", dto.getPlateNumber());

        // Check for duplicate plate number
        if (vehicleRepository.existsByPlateNumber(dto.getPlateNumber())) {
            throw new IllegalArgumentException("Vehicle with plate number " + dto.getPlateNumber() + " already exists");
        }

        Vehicle vehicle = Vehicle.builder()
                .plateNumber(dto.getPlateNumber())
                .model(dto.getModel())
                .manufacturer(dto.getManufacturer())
                .year(dto.getYear())
                .status(dto.getStatus())
                .build();

        Vehicle savedVehicle = vehicleRepository.save(vehicle);
        log.info("Vehicle created successfully with ID: {}", savedVehicle.getId());

        return savedVehicle.getId();
    }

    @Override
    @Transactional(readOnly = true)
    public VehicleResponseDTO getVehicle(UUID id) {
        log.info("Fetching vehicle with ID: {}", id);

        Vehicle vehicle = vehicleRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Vehicle not found with ID: " + id));

        return toDto(vehicle);
    }

    @Override
    @Transactional(readOnly = true)
    public List<VehicleResponseDTO> getAllVehicles() {
        log.info("Fetching all vehicles");

        return vehicleRepository.findAll()
                .stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public VehicleResponseDTO updateVehicle(UUID id, VehicleRequestDTO dto) {
        log.info("Updating vehicle with ID: {}", id);

        Vehicle vehicle = vehicleRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Vehicle not found with ID: " + id));

        // Check if plate number is being changed and if it already exists
        if (!vehicle.getPlateNumber().equals(dto.getPlateNumber())
                && vehicleRepository.existsByPlateNumber(dto.getPlateNumber())) {
            throw new IllegalArgumentException("Vehicle with plate number " + dto.getPlateNumber() + " already exists");
        }

        vehicle.setPlateNumber(dto.getPlateNumber());
        vehicle.setModel(dto.getModel());
        vehicle.setManufacturer(dto.getManufacturer());
        vehicle.setYear(dto.getYear());
        vehicle.setStatus(dto.getStatus());

        Vehicle updatedVehicle = vehicleRepository.save(vehicle);
        log.info("Vehicle updated successfully with ID: {}", updatedVehicle.getId());

        return toDto(updatedVehicle);
    }

    @Override
    @Transactional
    public void deleteVehicle(UUID id) {
        log.info("Deleting vehicle with ID: {}", id);

        if (!vehicleRepository.existsById(id)) {
            throw new ResourceNotFoundException("Vehicle not found with ID: " + id);
        }

        vehicleRepository.deleteById(id);
        log.info("Vehicle deleted successfully with ID: {}", id);
    }

    @Override
    @Transactional(readOnly = true)
    public VehicleResponseDTO findByPlate(String plateNumber) {
        log.info("Fetching vehicle with plate number: {}", plateNumber);

        Vehicle vehicle = vehicleRepository.findByPlateNumber(plateNumber)
                .orElseThrow(() -> new ResourceNotFoundException("Vehicle not found with plate number: " + plateNumber));

        return toDto(vehicle);
    }

    @Override
    @Transactional(readOnly = true)
    public VehicleStatsDTO getVehicleStats() {
        log.info("Fetching vehicle statistics");

        long totalVehicles = vehicleRepository.count();
        long availableVehicles = vehicleRepository.countByStatus("Available");
        long rentedVehicles = vehicleRepository.countByStatus("Rented");
        long maintenanceVehicles = vehicleRepository.countByStatus("Maintenance");

        return VehicleStatsDTO.builder()
                .totalVehicles(totalVehicles)
                .availableVehicles(availableVehicles)
                .rentedVehicles(rentedVehicles)
                .maintenanceVehicles(maintenanceVehicles)
                .build();
    }

    private VehicleResponseDTO toDto(Vehicle vehicle) {
        return VehicleResponseDTO.builder()
                .id(vehicle.getId())
                .plateNumber(vehicle.getPlateNumber())
                .model(vehicle.getModel())
                .manufacturer(vehicle.getManufacturer())
                .year(vehicle.getYear())
                .status(vehicle.getStatus())
                .createdAt(vehicle.getCreatedAt())
                .updatedAt(vehicle.getUpdatedAt())
                .build();
    }
}