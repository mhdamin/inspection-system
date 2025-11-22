package com.muvs.inspection_system.service;

import com.muvs.inspection_system.dto.SubChecklistRequestDTO;
import com.muvs.inspection_system.dto.SubChecklistResponseDTO;

import java.util.List;
import java.util.UUID;

public interface SubChecklistService {
    
    UUID createSubChecklist(SubChecklistRequestDTO dto);
    
    SubChecklistResponseDTO getSubChecklist(UUID id);
    
    List<SubChecklistResponseDTO> getSubChecklistsByChecklist(UUID checklistId);
    
    List<SubChecklistResponseDTO> getAllSubChecklists();
    
    SubChecklistResponseDTO updateSubChecklist(UUID id, SubChecklistRequestDTO dto);
    
    void deleteSubChecklist(UUID id);
}