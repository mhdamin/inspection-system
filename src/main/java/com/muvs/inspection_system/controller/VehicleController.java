package com.muvs.inspection_system.controller;

import com.muvs.inspection_system.dto.VehicleRequestDTO;
import com.muvs.inspection_system.dto.VehicleResponseDTO;
import com.muvs.inspection_system.dto.VehicleStatsDTO;
import com.muvs.inspection_system.service.VehicleService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@Slf4j
@RestController
@RequestMapping("/api/vehicles")
@RequiredArgsConstructor
public class VehicleController {

    private final VehicleService vehicleService;

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<UUID> createVehicle(@Valid @RequestBody VehicleRequestDTO dto) {
        log.info("POST /api/vehicles - Creating vehicle with plate: {}", dto.getPlateNumber());
        UUID vehicleId = vehicleService.createVehicle(dto);
        return new ResponseEntity<>(vehicleId, HttpStatus.CREATED);
    }

    @GetMapping("/{id}")
    public ResponseEntity<VehicleResponseDTO> getVehicle(@PathVariable UUID id) {
        log.info("GET /api/vehicles/{} - Fetching vehicle", id);
        VehicleResponseDTO vehicle = vehicleService.getVehicle(id);
        return ResponseEntity.ok(vehicle);
    }

    @GetMapping
    public ResponseEntity<List<VehicleResponseDTO>> getAllVehicles() {
        log.info("GET /api/vehicles - Fetching all vehicles");
        List<VehicleResponseDTO> vehicles = vehicleService.getAllVehicles();
        return ResponseEntity.ok(vehicles);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<VehicleResponseDTO> updateVehicle(
            @PathVariable UUID id,
            @Valid @RequestBody VehicleRequestDTO dto) {
        log.info("PUT /api/vehicles/{} - Updating vehicle", id);
        VehicleResponseDTO updatedVehicle = vehicleService.updateVehicle(id, dto);
        return ResponseEntity.ok(updatedVehicle);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> deleteVehicle(@PathVariable UUID id) {
        log.info("DELETE /api/vehicles/{} - Deleting vehicle", id);
        vehicleService.deleteVehicle(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/plate/{plateNumber}")
    public ResponseEntity<VehicleResponseDTO> findByPlateNumber(@PathVariable String plateNumber) {
        log.info("GET /api/vehicles/plate/{} - Fetching vehicle by plate number", plateNumber);
        VehicleResponseDTO vehicle = vehicleService.findByPlate(plateNumber);
        return ResponseEntity.ok(vehicle);
    }

    @GetMapping("/stats")
    public ResponseEntity<VehicleStatsDTO> getVehicleStats() {
        log.info("GET /api/vehicles/stats - Fetching vehicle stats");
        VehicleStatsDTO stats = vehicleService.getVehicleStats();
        return ResponseEntity.ok(stats);
    }
}