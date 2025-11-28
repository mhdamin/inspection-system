package com.muvs.inspection_system.service;

import com.muvs.inspection_system.dto.VehicleRequestDTO;
import com.muvs.inspection_system.dto.VehicleResponseDTO;
import com.muvs.inspection_system.dto.VehicleStatsDTO;

import java.util.List;
import java.util.UUID;

public interface VehicleService {

    UUID createVehicle(VehicleRequestDTO dto);

    VehicleResponseDTO getVehicle(UUID id);

    List<VehicleResponseDTO> getAllVehicles();

    VehicleResponseDTO updateVehicle(UUID id, VehicleRequestDTO dto);

    void deleteVehicle(UUID id);

    VehicleResponseDTO findByPlate(String plateNumber);

    VehicleStatsDTO getVehicleStats();
}