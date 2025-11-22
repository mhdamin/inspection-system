package com.muvs.inspection_system.controller;

import com.muvs.inspection_system.dto.SubChecklistRequestDTO;
import com.muvs.inspection_system.dto.SubChecklistResponseDTO;
import com.muvs.inspection_system.service.SubChecklistService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
// import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.List;
import java.util.UUID;

@Slf4j
@RestController
@RequestMapping("/api/sub-checklists")
@RequiredArgsConstructor
public class SubChecklistController {
    
    private final SubChecklistService subChecklistService;
    
    @PostMapping
    public ResponseEntity<SubChecklistResponseDTO> createSubChecklist(
            @Valid @RequestBody SubChecklistRequestDTO dto) {
        log.info("POST /api/sub-checklists - Creating sub-checklist for checklist ID: {}", 
                dto.getChecklistId());
        
        UUID subChecklistId = subChecklistService.createSubChecklist(dto);
        SubChecklistResponseDTO createdSubChecklist = subChecklistService.getSubChecklist(subChecklistId);
        
        // Build Location header
        URI location = ServletUriComponentsBuilder
                .fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(subChecklistId)
                .toUri();
        
        return ResponseEntity.created(location).body(createdSubChecklist);
    }
    
    @GetMapping("/{id}")
    public ResponseEntity<SubChecklistResponseDTO> getSubChecklist(@PathVariable UUID id) {
        log.info("GET /api/sub-checklists/{} - Fetching sub-checklist", id);
        SubChecklistResponseDTO subChecklist = subChecklistService.getSubChecklist(id);
        return ResponseEntity.ok(subChecklist);
    }
    
    @GetMapping("/checklist/{checklistId}")
    public ResponseEntity<List<SubChecklistResponseDTO>> getSubChecklistsByChecklist(
            @PathVariable UUID checklistId) {
        log.info("GET /api/sub-checklists/checklist/{} - Fetching sub-checklists for checklist", 
                checklistId);
        List<SubChecklistResponseDTO> subChecklists = 
                subChecklistService.getSubChecklistsByChecklist(checklistId);
        return ResponseEntity.ok(subChecklists);
    }
    
    @GetMapping
    public ResponseEntity<List<SubChecklistResponseDTO>> getAllSubChecklists() {
        log.info("GET /api/sub-checklists - Fetching all sub-checklists");
        List<SubChecklistResponseDTO> subChecklists = subChecklistService.getAllSubChecklists();
        return ResponseEntity.ok(subChecklists);
    }
    
    @PutMapping("/{id}")
    public ResponseEntity<SubChecklistResponseDTO> updateSubChecklist(
            @PathVariable UUID id,
            @Valid @RequestBody SubChecklistRequestDTO dto) {
        log.info("PUT /api/sub-checklists/{} - Updating sub-checklist", id);
        SubChecklistResponseDTO updatedSubChecklist = 
                subChecklistService.updateSubChecklist(id, dto);
        return ResponseEntity.ok(updatedSubChecklist);
    }
    
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteSubChecklist(@PathVariable UUID id) {
        log.info("DELETE /api/sub-checklists/{} - Deleting sub-checklist", id);
        subChecklistService.deleteSubChecklist(id);
        return ResponseEntity.noContent().build();
    }
}