package com.muvs.inspection_system.service;

import com.muvs.inspection_system.dto.ChecklistRequestDTO;
import com.muvs.inspection_system.dto.ChecklistResponseDTO;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

public interface ChecklistService {
    
    UUID createChecklist(ChecklistRequestDTO dto);
    
    ChecklistResponseDTO getChecklist(UUID id);
    
    List<ChecklistResponseDTO> getAllChecklists();
    
    ChecklistResponseDTO updateChecklist(UUID id, ChecklistRequestDTO dto);
    
    void deleteChecklist(UUID id);
    
    ChecklistResponseDTO findByChecklistNumber(String checklistNumber);
    
    List<ChecklistResponseDTO> searchChecklists(
            String checklistNumber,
            String plateNumber,
            String customerName,
            LocalDate startDate,
            LocalDate endDate
    );
}