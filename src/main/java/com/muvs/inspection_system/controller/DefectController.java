package com.muvs.inspection_system.controller;

import com.muvs.inspection_system.dto.DefectImageResponseDTO;
import com.muvs.inspection_system.dto.DefectRequestDTO;
import com.muvs.inspection_system.dto.DefectResponseDTO;
import com.muvs.inspection_system.service.DefectImageService;
import com.muvs.inspection_system.service.DefectService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.UUID;

@Slf4j
@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class DefectController {
    
    private final DefectService defectService;
    private final DefectImageService defectImageService;
    
    /**
     * Create a new defect for a checklist
     * POST /api/checklists/{checklistId}/defects
     */
    @PostMapping("/checklists/{checklistId}/defects")
    public ResponseEntity<DefectResponseDTO> createDefect(
            @PathVariable UUID checklistId,
            @Valid @RequestBody DefectRequestDTO dto) {
        log.info("POST /api/checklists/{}/defects - Creating defect", checklistId);
        DefectResponseDTO defect = defectService.createDefect(checklistId, dto);
        return new ResponseEntity<>(defect, HttpStatus.CREATED);
    }
    
    /**
     * Get all defects for a checklist
     * GET /api/checklists/{checklistId}/defects
     */
    @GetMapping("/checklists/{checklistId}/defects")
    public ResponseEntity<List<DefectResponseDTO>> getDefectsByChecklist(
            @PathVariable UUID checklistId) {
        log.info("GET /api/checklists/{}/defects - Fetching defects for checklist", checklistId);
        List<DefectResponseDTO> defects = defectService.getDefectsByChecklist(checklistId);
        return ResponseEntity.ok(defects);
    }
    
    /**
     * Get a specific defect by ID
     * GET /api/defects/{defectId}
     */
    @GetMapping("/defects/{defectId}")
    public ResponseEntity<DefectResponseDTO> getDefect(@PathVariable Long defectId) {
        log.info("GET /api/defects/{} - Fetching defect", defectId);
        DefectResponseDTO defect = defectService.getDefect(defectId);
        return ResponseEntity.ok(defect);
    }
    
    /**
     * Update a defect
     * PUT /api/defects/{defectId}
     */
    @PutMapping("/defects/{defectId}")
    public ResponseEntity<DefectResponseDTO> updateDefect(
            @PathVariable Long defectId,
            @Valid @RequestBody DefectRequestDTO dto) {
        log.info("PUT /api/defects/{} - Updating defect", defectId);
        DefectResponseDTO updatedDefect = defectService.updateDefect(defectId, dto);
        return ResponseEntity.ok(updatedDefect);
    }
    
    /**
     * Delete a defect
     * DELETE /api/defects/{defectId}
     */
    @DeleteMapping("/defects/{defectId}")
    public ResponseEntity<Void> deleteDefect(@PathVariable Long defectId) {
        log.info("DELETE /api/defects/{} - Deleting defect", defectId);
        defectService.deleteDefect(defectId);
        return ResponseEntity.noContent().build();
    }
    
    /**
     * Upload an image to a defect
     * POST /api/defects/{defectId}/images
     */
    @PostMapping(value = "/defects/{defectId}/images", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<DefectImageResponseDTO> uploadImage(
            @PathVariable Long defectId,
            @RequestParam("file") MultipartFile file,
            @RequestParam(value = "uploadedBy", required = false) Long uploadedBy) {
        log.info("POST /api/defects/{}/images - Uploading image", defectId);
        DefectImageResponseDTO image = defectImageService.uploadImage(defectId, file, uploadedBy);
        return new ResponseEntity<>(image, HttpStatus.CREATED);
    }
    
    /**
     * Get all images for a defect
     * GET /api/defects/{defectId}/images
     */
    @GetMapping("/defects/{defectId}/images")
    public ResponseEntity<List<DefectImageResponseDTO>> getImagesByDefect(
            @PathVariable Long defectId) {
        log.info("GET /api/defects/{}/images - Fetching images for defect", defectId);
        List<DefectImageResponseDTO> images = defectImageService.getImagesByDefect(defectId);
        return ResponseEntity.ok(images);
    }
    
    /**
     * Delete an image
     * DELETE /api/defects/images/{imageId}
     */
    @DeleteMapping("/defects/images/{imageId}")
    public ResponseEntity<Void> deleteImage(@PathVariable Long imageId) {
        log.info("DELETE /api/defects/images/{} - Deleting image", imageId);
        defectImageService.deleteImage(imageId);
        return ResponseEntity.noContent().build();
    }
}