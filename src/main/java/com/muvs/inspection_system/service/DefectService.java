package com.muvs.inspection_system.service;

import com.muvs.inspection_system.dto.DefectRequestDTO;
import com.muvs.inspection_system.dto.DefectResponseDTO;

import java.util.List;
import java.util.UUID;

public interface DefectService {

    DefectResponseDTO createDefect(UUID checklistId, DefectRequestDTO dto);

    DefectResponseDTO getDefect(Long defectId);

    List<DefectResponseDTO> getDefectsByChecklist(UUID checklistId);

    DefectResponseDTO updateDefect(Long defectId, DefectRequestDTO dto);

    void deleteDefect(Long defectId);
}