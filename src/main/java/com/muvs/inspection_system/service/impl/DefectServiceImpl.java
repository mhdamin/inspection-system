package com.muvs.inspection_system.service.impl;

import com.muvs.inspection_system.dto.DefectImageResponseDTO;
import com.muvs.inspection_system.dto.DefectRequestDTO;
import com.muvs.inspection_system.dto.DefectResponseDTO;
import com.muvs.inspection_system.entity.Defect;
import com.muvs.inspection_system.entity.DefectImage;
import com.muvs.inspection_system.exception.ResourceNotFoundException;
import com.muvs.inspection_system.entity.Checklist;
import com.muvs.inspection_system.repository.ChecklistRepository;
import com.muvs.inspection_system.repository.DefectRepository;
import com.muvs.inspection_system.service.DefectService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class DefectServiceImpl implements DefectService {
    
    private final DefectRepository defectRepository;
    private final ChecklistRepository checklistRepository;
    
    @Override
    @Transactional
    public DefectResponseDTO createDefect(UUID checklistId, DefectRequestDTO dto) {
        log.info("Creating defect for checklist ID: {}", checklistId);

        Checklist checklist = checklistRepository.findById(checklistId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Checklist not found with ID: " + checklistId));

        Defect defect = Defect.builder()
                .checklist(checklist)
                .itemId(dto.getItemId())
                .defectType(dto.getDefectType())
                .description(dto.getDescription())
                .diagramX(dto.getDiagramX())
                .diagramY(dto.getDiagramY())
                .build();

        Defect savedDefect = defectRepository.save(defect);
        log.info("Defect created successfully with ID: {}", savedDefect.getDefectId());

        return toDto(savedDefect);
    }
    
    @Override
    @Transactional(readOnly = true)
    public DefectResponseDTO getDefect(Long defectId) {
        log.info("Fetching defect with ID: {}", defectId);
        
        Defect defect = defectRepository.findById(defectId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Defect not found with ID: " + defectId));
        
        return toDto(defect);
    }
    
        @Override
        @Transactional(readOnly = true)
        public List<DefectResponseDTO> getDefectsByChecklist(UUID checklistId) {
            log.info("Fetching defects for checklist ID: {}", checklistId);
    
            return defectRepository.findByChecklist_IdOrderByCreatedAtDesc(checklistId)
                    .stream()
                    .map(this::toDto)
                    .collect(Collectors.toList());
        }    
    @Override
    @Transactional
    public DefectResponseDTO updateDefect(Long defectId, DefectRequestDTO dto) {
        log.info("Updating defect with ID: {}", defectId);
        
        Defect defect = defectRepository.findById(defectId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Defect not found with ID: " + defectId));
        
        defect.setItemId(dto.getItemId());
        defect.setDefectType(dto.getDefectType());
        defect.setDescription(dto.getDescription());
        defect.setDiagramX(dto.getDiagramX());
        defect.setDiagramY(dto.getDiagramY());
        
        Defect updatedDefect = defectRepository.save(defect);
        log.info("Defect updated successfully with ID: {}", updatedDefect.getDefectId());
        
        return toDto(updatedDefect);
    }
    
    @Override
    @Transactional
    public void deleteDefect(Long defectId) {
        log.info("Deleting defect with ID: {}", defectId);
        
        if (!defectRepository.existsById(defectId)) {
            throw new ResourceNotFoundException("Defect not found with ID: " + defectId);
        }
        
        // Images will be automatically deleted due to cascade
        defectRepository.deleteById(defectId);
        log.info("Defect deleted successfully with ID: {}", defectId);
    }
    
    private DefectResponseDTO toDto(Defect defect) {
        List<DefectImageResponseDTO> imageDtos = defect.getImages()
                .stream()
                .map(this::toImageDto)
                .collect(Collectors.toList());
        
        return DefectResponseDTO.builder()
                .defectId(defect.getDefectId())
                .checklistId(defect.getChecklist().getId())
                .itemId(defect.getItemId())
                .defectType(defect.getDefectType())
                .description(defect.getDescription())
                .diagramX(defect.getDiagramX())
                .diagramY(defect.getDiagramY())
                .createdAt(defect.getCreatedAt())
                .updatedAt(defect.getUpdatedAt())
                .images(imageDtos)
                .build();
    }
    
    private DefectImageResponseDTO toImageDto(DefectImage image) {
        return DefectImageResponseDTO.builder()
                .imageId(image.getImageId())
                .defectId(image.getDefectId())
                .imageUrl(image.getImageUrl())
                .fileName(image.getFileName())
                .fileSize(image.getFileSize())
                .uploadedBy(image.getUploadedBy())
                .uploadedAt(image.getUploadedAt())
                .build();
    }
}