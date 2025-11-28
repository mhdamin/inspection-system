package com.muvs.inspection_system.service.impl;

import com.muvs.inspection_system.dto.VehicleRequestDTO;
import com.muvs.inspection_system.dto.VehicleResponseDTO;
import com.muvs.inspection_system.dto.VehicleStatsDTO;
import com.muvs.inspection_system.entity.Vehicle;
import com.muvs.inspection_system.exception.ResourceNotFoundException;
import com.muvs.inspection_system.repository.VehicleRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("VehicleService Unit Tests")
class VehicleServiceImplTest {

    @Mock
    private VehicleRepository vehicleRepository;

    @InjectMocks
    private VehicleServiceImpl vehicleService;

    private VehicleRequestDTO validVehicleRequest;
    private Vehicle mockVehicle;
    private UUID vehicleId;

    @BeforeEach
    void setUp() {
        vehicleId = UUID.randomUUID();

        validVehicleRequest = VehicleRequestDTO.builder()
                .plateNumber("ABC-123")
                .manufacturer("Toyota")
                .model("Camry")
                .year(2023)
                .status("Available")
                .build();

        mockVehicle = Vehicle.builder()
                .plateNumber("ABC-123")
                .manufacturer("Toyota")
                .model("Camry")
                .year(2023)
                .status("Available")
                .build();

        // Set ID manually since it's in BaseEntity
        mockVehicle.setId(vehicleId);
        mockVehicle.setCreatedAt(LocalDateTime.now());
        mockVehicle.setUpdatedAt(LocalDateTime.now());
    }

    @Test
    @DisplayName("Should create vehicle successfully with valid data")
    void createVehicle_ValidData_ReturnsVehicleId() {
        // Arrange
        when(vehicleRepository.existsByPlateNumber(anyString())).thenReturn(false);
        when(vehicleRepository.save(any(Vehicle.class))).thenReturn(mockVehicle);

        // Act
        UUID result = vehicleService.createVehicle(validVehicleRequest);

        // Assert
        assertThat(result).isNotNull();
        assertThat(result).isEqualTo(vehicleId);
        verify(vehicleRepository, times(1)).existsByPlateNumber("ABC-123");
        verify(vehicleRepository, times(1)).save(any(Vehicle.class));
    }

    @Test
    @DisplayName("Should throw exception when creating vehicle with duplicate plate number")
    void createVehicle_DuplicatePlateNumber_ThrowsException() {
        // Arrange
        when(vehicleRepository.existsByPlateNumber(anyString())).thenReturn(true);

        // Act & Assert
        assertThatThrownBy(() -> vehicleService.createVehicle(validVehicleRequest))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("already exists");

        verify(vehicleRepository, times(1)).existsByPlateNumber("ABC-123");
        verify(vehicleRepository, never()).save(any(Vehicle.class));
    }

    @Test
    @DisplayName("Should retrieve vehicle by ID successfully")
    void getVehicle_ExistingId_ReturnsVehicleDTO() {
        // Arrange
        when(vehicleRepository.findById(vehicleId)).thenReturn(Optional.of(mockVehicle));

        // Act
        VehicleResponseDTO result = vehicleService.getVehicle(vehicleId);

        // Assert
        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(vehicleId);
        assertThat(result.getPlateNumber()).isEqualTo("ABC-123");
        assertThat(result.getManufacturer()).isEqualTo("Toyota");
        assertThat(result.getModel()).isEqualTo("Camry");
        assertThat(result.getYear()).isEqualTo(2023);
        assertThat(result.getStatus()).isEqualTo("Available");
        verify(vehicleRepository, times(1)).findById(vehicleId);
    }

    @Test
    @DisplayName("Should throw exception when vehicle not found by ID")
    void getVehicle_NonExistentId_ThrowsResourceNotFoundException() {
        // Arrange
        UUID nonExistentId = UUID.randomUUID();
        when(vehicleRepository.findById(nonExistentId)).thenReturn(Optional.empty());

        // Act & Assert
        assertThatThrownBy(() -> vehicleService.getVehicle(nonExistentId))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("Vehicle not found");

        verify(vehicleRepository, times(1)).findById(nonExistentId);
    }

    @Test
    @DisplayName("Should retrieve all vehicles successfully")
    void getAllVehicles_MultipleVehicles_ReturnsVehicleList() {
        // Arrange
        Vehicle vehicle2 = Vehicle.builder()
                .plateNumber("XYZ-789")
                .manufacturer("Honda")
                .model("Accord")
                .year(2022)
                .status("Rented")
                .build();

        vehicle2.setId(UUID.randomUUID());
        vehicle2.setCreatedAt(LocalDateTime.now());
        vehicle2.setUpdatedAt(LocalDateTime.now());

        when(vehicleRepository.findAll()).thenReturn(Arrays.asList(mockVehicle, vehicle2));

        // Act
        List<VehicleResponseDTO> result = vehicleService.getAllVehicles();

        // Assert
        assertThat(result).isNotNull();
        assertThat(result).hasSize(2);
        assertThat(result.get(0).getPlateNumber()).isEqualTo("ABC-123");
        assertThat(result.get(1).getPlateNumber()).isEqualTo("XYZ-789");
        verify(vehicleRepository, times(1)).findAll();
    }

    @Test
    @DisplayName("Should update vehicle successfully")
    void updateVehicle_ValidData_ReturnsUpdatedVehicle() {
        // Arrange
        VehicleRequestDTO updateRequest = VehicleRequestDTO.builder()
                .plateNumber("ABC-123")  // Same plate number, so no duplicate check needed
                .manufacturer("Toyota")
                .model("Camry XLE")
                .year(2024)
                .status("Maintenance")
                .build();

        when(vehicleRepository.findById(vehicleId)).thenReturn(Optional.of(mockVehicle));
        when(vehicleRepository.save(any(Vehicle.class))).thenReturn(mockVehicle);

        // Act
        VehicleResponseDTO result = vehicleService.updateVehicle(vehicleId, updateRequest);

        // Assert
        assertThat(result).isNotNull();
        verify(vehicleRepository, times(1)).findById(vehicleId);
        verify(vehicleRepository, times(1)).save(any(Vehicle.class));
    }

    @Test
    @DisplayName("Should throw exception when updating non-existent vehicle")
    void updateVehicle_NonExistentId_ThrowsResourceNotFoundException() {
        // Arrange
        UUID nonExistentId = UUID.randomUUID();
        when(vehicleRepository.findById(nonExistentId)).thenReturn(Optional.empty());

        // Act & Assert
        assertThatThrownBy(() -> vehicleService.updateVehicle(nonExistentId, validVehicleRequest))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("Vehicle not found");

        verify(vehicleRepository, times(1)).findById(nonExistentId);
        verify(vehicleRepository, never()).save(any(Vehicle.class));
    }

    @Test
    @DisplayName("Should throw exception when updating to duplicate plate number")
    void updateVehicle_DuplicatePlateNumber_ThrowsException() {
        // Arrange
        VehicleRequestDTO updateRequest = VehicleRequestDTO.builder()
                .plateNumber("XYZ-789")
                .manufacturer("Toyota")
                .model("Camry")
                .year(2023)
                .status("Available")
                .build();

        when(vehicleRepository.findById(vehicleId)).thenReturn(Optional.of(mockVehicle));
        when(vehicleRepository.existsByPlateNumber("XYZ-789")).thenReturn(true);

        // Act & Assert
        assertThatThrownBy(() -> vehicleService.updateVehicle(vehicleId, updateRequest))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("already exists");

        verify(vehicleRepository, times(1)).findById(vehicleId);
        verify(vehicleRepository, never()).save(any(Vehicle.class));
    }

    @Test
    @DisplayName("Should delete vehicle successfully")
    void deleteVehicle_ExistingId_DeletesSuccessfully() {
        // Arrange
        when(vehicleRepository.existsById(vehicleId)).thenReturn(true);
        doNothing().when(vehicleRepository).deleteById(vehicleId);

        // Act
        vehicleService.deleteVehicle(vehicleId);

        // Assert
        verify(vehicleRepository, times(1)).existsById(vehicleId);
        verify(vehicleRepository, times(1)).deleteById(vehicleId);
    }

    @Test
    @DisplayName("Should throw exception when deleting non-existent vehicle")
    void deleteVehicle_NonExistentId_ThrowsResourceNotFoundException() {
        // Arrange
        UUID nonExistentId = UUID.randomUUID();
        when(vehicleRepository.existsById(nonExistentId)).thenReturn(false);

        // Act & Assert
        assertThatThrownBy(() -> vehicleService.deleteVehicle(nonExistentId))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("Vehicle not found");

        verify(vehicleRepository, times(1)).existsById(nonExistentId);
        verify(vehicleRepository, never()).deleteById(any());
    }

    @Test
    @DisplayName("Should find vehicle by plate number successfully")
    void findByPlate_ExistingPlateNumber_ReturnsVehicleDTO() {
        // Arrange
        when(vehicleRepository.findByPlateNumber("ABC-123")).thenReturn(Optional.of(mockVehicle));

        // Act
        VehicleResponseDTO result = vehicleService.findByPlate("ABC-123");

        // Assert
        assertThat(result).isNotNull();
        assertThat(result.getPlateNumber()).isEqualTo("ABC-123");
        verify(vehicleRepository, times(1)).findByPlateNumber("ABC-123");
    }

    @Test
    @DisplayName("Should throw exception when plate number not found")
    void findByPlate_NonExistentPlateNumber_ThrowsResourceNotFoundException() {
        // Arrange
        when(vehicleRepository.findByPlateNumber("INVALID")).thenReturn(Optional.empty());

        // Act & Assert
        assertThatThrownBy(() -> vehicleService.findByPlate("INVALID"))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("Vehicle not found");

        verify(vehicleRepository, times(1)).findByPlateNumber("INVALID");
    }

    @Test
    @DisplayName("Should calculate vehicle statistics correctly")
    void getVehicleStats_MultipleVehicles_ReturnsCorrectStats() {
        // Arrange
        when(vehicleRepository.count()).thenReturn(30L);
        when(vehicleRepository.countByStatus("Available")).thenReturn(15L);
        when(vehicleRepository.countByStatus("Rented")).thenReturn(10L);
        when(vehicleRepository.countByStatus("Maintenance")).thenReturn(5L);

        // Act
        VehicleStatsDTO result = vehicleService.getVehicleStats();

        // Assert
        assertThat(result).isNotNull();
        assertThat(result.getTotalVehicles()).isEqualTo(30L);
        assertThat(result.getAvailableVehicles()).isEqualTo(15L);
        assertThat(result.getRentedVehicles()).isEqualTo(10L);
        assertThat(result.getMaintenanceVehicles()).isEqualTo(5L);

        verify(vehicleRepository, times(1)).count();
        verify(vehicleRepository, times(1)).countByStatus("Available");
        verify(vehicleRepository, times(1)).countByStatus("Rented");
        verify(vehicleRepository, times(1)).countByStatus("Maintenance");
    }

    @Test
    @DisplayName("Should handle empty vehicle list")
    void getAllVehicles_EmptyRepository_ReturnsEmptyList() {
        // Arrange
        when(vehicleRepository.findAll()).thenReturn(Arrays.asList());

        // Act
        List<VehicleResponseDTO> result = vehicleService.getAllVehicles();

        // Assert
        assertThat(result).isNotNull();
        assertThat(result).isEmpty();
        verify(vehicleRepository, times(1)).findAll();
    }
}
