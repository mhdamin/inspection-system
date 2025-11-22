// package com.muvs.inspection_system.tests;

// import com.muvs.inspection_system.dto.SubChecklistRequestDTO;
// import com.muvs.inspection_system.dto.SubChecklistResponseDTO;
// import com.muvs.inspection_system.entity.Checklist;
// import com.muvs.inspection_system.entity.SubChecklist;
// import com.muvs.inspection_system.entity.Vehicle;
// import com.muvs.inspection_system.enums.SubChecklistType;
// import com.muvs.inspection_system.exception.ResourceNotFoundException;
// import com.muvs.inspection_system.repository.ChecklistRepository;
// import com.muvs.inspection_system.repository.SubChecklistRepository;
// import com.muvs.inspection_system.repository.VehicleRepository;
// import org.junit.jupiter.api.BeforeEach;
// import org.junit.jupiter.api.Test;
// import org.junit.jupiter.api.extension.ExtendWith;
// import org.mockito.InjectMocks;
// import org.mockito.Mock;
// import org.mockito.junit.jupiter.MockitoExtension;

// import java.time.LocalDate;
// import java.time.LocalDateTime;
// import java.util.Arrays;
// import java.util.List;
// import java.util.Optional;
// import java.util.UUID;

// import static org.junit.jupiter.api.Assertions.*;
// import static org.mockito.ArgumentMatchers.any;
// import static org.mockito.Mockito.*;

// /**
//  * Unit test suggestions for SubChecklistService
//  * 
//  * Test Coverage:
//  * 1. createSubChecklist_success - Happy path with valid data
//  * 2. createSubChecklist_missingChecklist_throws - Parent checklist not found
//  * 3. createSubChecklist_missingVehicle_throws - Vehicle not found when vehicleId provided
//  * 4. createSubChecklist_duplicateNumber_throws - Duplicate sub-checklist number
//  * 5. createSubChecklist_autoGenerateNumber_success - Auto-generate sub-checklist number
//  * 6. updateSubChecklist_changesFields - Successfully update fields
//  * 7. updateSubChecklist_notFound_throws - Sub-checklist not found
//  * 8. getSubChecklistsByChecklist_returnsOrderedList - Returns list ordered by timestamp
//  * 9. getSubChecklistsByChecklist_checklistNotFound_throws - Parent checklist not found
//  * 10. deleteSubChecklist_success - Successfully delete sub-checklist
//  */
// @ExtendWith(MockitoExtension.class)
// class SubChecklistServiceImplTest {
    
//     @Mock
//     private SubChecklistRepository subChecklistRepository;
    
//     @Mock
//     private ChecklistRepository checklistRepository;
    
//     @Mock
//     private VehicleRepository vehicleRepository;
    
//     @InjectMocks
//     private SubChecklistServiceImpl subChecklistService;
    
//     private UUID checklistId;
//     private UUID vehicleId;
//     private UUID subChecklistId;
//     private Checklist mockChecklist;
//     private Vehicle mockVehicle;
//     private SubChecklist mockSubChecklist;
    
//     @BeforeEach
//     void setUp() {
//         checklistId = UUID.randomUUID();
//         vehicleId = UUID.randomUUID();
//         subChecklistId = UUID.randomUUID();
        
//         mockChecklist = Checklist.builder()
//                 .id(checklistId)
//                 .checklistNumber("CL-2025-001")
//                 .rentalStartDate(LocalDate.now())
//                 .customerName("John Doe")
//                 .customerPhone("1234567890")
//                 .staffName("Staff A")
//                 .rentalType("Start")
//                 .build();
        
//         mockVehicle = Vehicle.builder()
//                 .id(vehicleId)
//                 .plateNumber("ABC1234")
//                 .model("Toyota Camry")
//                 .manufacturer("Toyota")
//                 .year(2023)
//                 .status("Available")
//                 .build();
        
//         mockSubChecklist = SubChecklist.builder()
//                 .id(subChecklistId)
//                 .type(SubChecklistType.TEMPORARY)
//                 .subChecklistNumber("CL-2025-001-SUB-20250116120000")
//                 .timestamp(LocalDateTime.now())
//                 .staffName("Staff B")
//                 .remarks("Temporary replacement")
//                 .vehicleId(vehicleId)
//                 .checklist(mockChecklist)
//                 .build();
//     }
    
//     @Test
//     void createSubChecklist_success() {
//         // Arrange
//         SubChecklistRequestDTO dto = SubChecklistRequestDTO.builder()
//                 .type(SubChecklistType.TEMPORARY)
//                 .subChecklistNumber("CL-2025-001-SUB-001")
//                 .timestamp(LocalDateTime.now())
//                 .staffName("Staff B")
//                 .remarks("Temporary replacement")
//                 .vehicleId(vehicleId)
//                 .checklistId(checklistId)
//                 .build();
        
//         when(checklistRepository.findById(checklistId)).thenReturn(Optional.of(mockChecklist));
//         when(vehicleRepository.findById(vehicleId)).thenReturn(Optional.of(mockVehicle));
//         when(subChecklistRepository.existsBySubChecklistNumber(anyString())).thenReturn(false);
//         when(subChecklistRepository.save(any(SubChecklist.class))).thenReturn(mockSubChecklist);
        
//         // Act
//         UUID result = subChecklistService.createSubChecklist(dto);
        
//         // Assert
//         assertNotNull(result);
//         assertEquals(subChecklistId, result);
//         verify(checklistRepository).findById(checklistId);
//         verify(vehicleRepository).findById(vehicleId);
//         verify(subChecklistRepository).save(any(SubChecklist.class));
//     }
    
//     @Test
//     void createSubChecklist_missingChecklist_throws() {
//         // Arrange
//         SubChecklistRequestDTO dto = SubChecklistRequestDTO.builder()
//                 .type(SubChecklistType.TEMPORARY)
//                 .staffName("Staff B")
//                 .checklistId(checklistId)
//                 .build();
        
//         when(checklistRepository.findById(checklistId)).thenReturn(Optional.empty());
        
//         // Act & Assert
//         assertThrows(ResourceNotFoundException.class, 
//                 () -> subChecklistService.createSubChecklist(dto));
//         verify(checklistRepository).findById(checklistId);
//         verify(subChecklistRepository, never()).save(any());
//     }
    
//     @Test
//     void createSubChecklist_missingVehicle_throws() {
//         // Arrange
//         SubChecklistRequestDTO dto = SubChecklistRequestDTO.builder()
//                 .type(SubChecklistType.TEMPORARY)
//                 .staffName("Staff B")
//                 .vehicleId(vehicleId)
//                 .checklistId(checklistId)
//                 .build();
        
//         when(checklistRepository.findById(checklistId)).thenReturn(Optional.of(mockChecklist));
//         when(vehicleRepository.findById(vehicleId)).thenReturn(Optional.empty());
        
//         // Act & Assert
//         assertThrows(ResourceNotFoundException.class, 
//                 () -> subChecklistService.createSubChecklist(dto));
//         verify(vehicleRepository).findById(vehicleId);
//         verify(subChecklistRepository, never()).save(any());
//     }
    
//     @Test
//     void createSubChecklist_duplicateNumber_throws() {
//         // Arrange
//         SubChecklistRequestDTO dto = SubChecklistRequestDTO.builder()
//                 .type(SubChecklistType.TEMPORARY)
//                 .subChecklistNumber("CL-2025-001-SUB-001")
//                 .staffName("Staff B")
//                 .checklistId(checklistId)
//                 .build();
        
//         when(checklistRepository.findById(checklistId)).thenReturn(Optional.of(mockChecklist));
//         when(subChecklistRepository.existsBySubChecklistNumber("CL-2025-001-SUB-001"))
//                 .thenReturn(true);
        
//         // Act & Assert
//         assertThrows(IllegalArgumentException.class, 
//                 () -> subChecklistService.createSubChecklist(dto));
//         verify(subChecklistRepository, never()).save(any());
//     }
    
//     @Test
//     void createSubChecklist_autoGenerateNumber_success() {
//         // Arrange
//         SubChecklistRequestDTO dto = SubChecklistRequestDTO.builder()
//                 .type(SubChecklistType.TEMPORARY)
//                 .staffName("Staff B")
//                 .checklistId(checklistId)
//                 .build();
        
//         when(checklistRepository.findById(checklistId)).thenReturn(Optional.of(mockChecklist));
//         when(subChecklistRepository.existsBySubChecklistNumber(anyString())).thenReturn(false);
//         when(subChecklistRepository.save(any(SubChecklist.class))).thenReturn(mockSubChecklist);
        
//         // Act
//         UUID result = subChecklistService.createSubChecklist(dto);
        
//         // Assert
//         assertNotNull(result);
//         verify(subChecklistRepository).save(any(SubChecklist.class));
//     }
    
//     @Test
//     void updateSubChecklist_changesFields() {
//         // Arrange
//         SubChecklistRequestDTO dto = SubChecklistRequestDTO.builder()
//                 .type(SubChecklistType.RETURN)
//                 .staffName("Staff C")
//                 .remarks("Updated remarks")
//                 .checklistId(checklistId)
//                 .build();
        
//         when(subChecklistRepository.findById(subChecklistId))
//                 .thenReturn(Optional.of(mockSubChecklist));
//         when(checklistRepository.findById(checklistId))
//                 .thenReturn(Optional.of(mockChecklist));
//         when(subChecklistRepository.save(any(SubChecklist.class)))
//                 .thenReturn(mockSubChecklist);
        
//         // Act
//         SubChecklistResponseDTO result = subChecklistService.updateSubChecklist(subChecklistId, dto);
        
//         // Assert
//         assertNotNull(result);
//         verify(subChecklistRepository).findById(subChecklistId);
//         verify(subChecklistRepository).save(any(SubChecklist.class));
//     }
    
//     @Test
//     void updateSubChecklist_notFound_throws() {
//         // Arrange
//         SubChecklistRequestDTO dto = SubChecklistRequestDTO.builder()
//                 .type(SubChecklistType.RETURN)
//                 .staffName("Staff C")
//                 .checklistId(checklistId)
//                 .build();
        
//         when(subChecklistRepository.findById(subChecklistId)).thenReturn(Optional.empty());
        
//         // Act & Assert
//         assertThrows(ResourceNotFoundException.class,
//                 () -> subChecklistService.updateSubChecklist(subChecklistId, dto));
//         verify(subChecklistRepository, never()).save(any());
//     }
    
//     @Test
//     void getSubChecklistsByChecklist_returnsOrderedList() {
//         // Arrange
//         SubChecklist sub1 = SubChecklist.builder()
//                 .id(UUID.randomUUID())
//                 .timestamp(LocalDateTime.now().minusHours(2))
//                 .checklist(mockChecklist)
//                 .type(SubChecklistType.TEMPORARY)
//                 .staffName("Staff A")
//                 .subChecklistNumber("SUB-001")
//                 .build();
        
//         SubChecklist sub2 = SubChecklist.builder()
//                 .id(UUID.randomUUID())
//                 .timestamp(LocalDateTime.now().minusHours(1))
//                 .checklist(mockChecklist)
//                 .type(SubChecklistType.RETURN)
//                 .staffName("Staff B")
//                 .subChecklistNumber("SUB-002")
//                 .build();
        
//         when(checklistRepository.existsById(checklistId)).thenReturn(true);
//         when(subChecklistRepository.findByChecklistIdOrderByTimestampAsc(checklistId))
//                 .thenReturn(Arrays.asList(sub1, sub2));
//         when(vehicleRepository.findById(any())).thenReturn(Optional.of(mockVehicle));
        
//         // Act
//         List<SubChecklistResponseDTO> result = 
//                 subChecklistService.getSubChecklistsByChecklist(checklistId);
        
//         // Assert
//         assertNotNull(result);
//         assertEquals(2, result.size());
//         verify(subChecklistRepository).findByChecklistIdOrderByTimestampAsc(checklistId);
//     }
    
//     @Test
//     void getSubChecklistsByChecklist_checklistNotFound_throws() {
//         // Arrange
//         when(checklistRepository.existsById(checklistId)).thenReturn(false);
        
//         // Act & Assert
//         assertThrows(ResourceNotFoundException.class,
//                 () -> subChecklistService.getSubChecklistsByChecklist(checklistId));
//         verify(subChecklistRepository, never()).findByChecklistIdOrderByTimestampAsc(any());
//     }
    
//     @Test
//     void deleteSubChecklist_success() {
//         // Arrange
//         when(subChecklistRepository.existsById(subChecklistId)).thenReturn(true);
        
//         // Act
//         subChecklistService.deleteSubChecklist(subChecklistId);
        
//         // Assert
//         verify(subChecklistRepository).existsById(subChecklistId);
//         verify(subChecklistRepository).deleteById(subChecklistId);
//     }
// }