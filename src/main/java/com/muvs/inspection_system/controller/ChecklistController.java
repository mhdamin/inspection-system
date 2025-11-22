package com.muvs.inspection_system.controller;

import com.muvs.inspection_system.dto.ChecklistRequestDTO;
import com.muvs.inspection_system.dto.ChecklistResponseDTO;
import com.muvs.inspection_system.service.ChecklistService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@Slf4j
@RestController
@RequestMapping("/api/checklists")
@RequiredArgsConstructor
public class ChecklistController {
    
    private final ChecklistService checklistService;
    
    @PostMapping
    public ResponseEntity<UUID> createChecklist(@Valid @RequestBody ChecklistRequestDTO dto) {
        log.info("POST /api/checklists - Creating checklist with number: {}", dto.getChecklistNumber());
        UUID checklistId = checklistService.createChecklist(dto);
        return new ResponseEntity<>(checklistId, HttpStatus.CREATED);
    }
    
    @GetMapping("/{id}")
    public ResponseEntity<ChecklistResponseDTO> getChecklist(@PathVariable UUID id) {
        log.info("GET /api/checklists/{} - Fetching checklist", id);
        ChecklistResponseDTO checklist = checklistService.getChecklist(id);
        return ResponseEntity.ok(checklist);
    }
    
    @GetMapping
    public ResponseEntity<List<ChecklistResponseDTO>> getAllChecklists() {
        log.info("GET /api/checklists - Fetching all checklists");
        List<ChecklistResponseDTO> checklists = checklistService.getAllChecklists();
        return ResponseEntity.ok(checklists);
    }
    
    @PutMapping("/{id}")
    public ResponseEntity<ChecklistResponseDTO> updateChecklist(
            @PathVariable UUID id,
            @Valid @RequestBody ChecklistRequestDTO dto) {
        log.info("PUT /api/checklists/{} - Updating checklist", id);
        ChecklistResponseDTO updatedChecklist = checklistService.updateChecklist(id, dto);
        return ResponseEntity.ok(updatedChecklist);
    }
    
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteChecklist(@PathVariable UUID id) {
        log.info("DELETE /api/checklists/{} - Deleting checklist", id);
        checklistService.deleteChecklist(id);
        return ResponseEntity.noContent().build();
    }
    
    @GetMapping("/number/{checklistNumber}")
    public ResponseEntity<ChecklistResponseDTO> findByChecklistNumber(
            @PathVariable String checklistNumber) {
        log.info("GET /api/checklists/number/{} - Fetching checklist by number", checklistNumber);
        ChecklistResponseDTO checklist = checklistService.findByChecklistNumber(checklistNumber);
        return ResponseEntity.ok(checklist);
    }
    
    @GetMapping("/search")
    public ResponseEntity<List<ChecklistResponseDTO>> searchChecklists(
            @RequestParam(required = false) String checklistNumber,
            @RequestParam(required = false) String plateNumber,
            @RequestParam(required = false) String customerName,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {
        
        log.info("GET /api/checklists/search - Searching checklists with filters");
        List<ChecklistResponseDTO> checklists = checklistService.searchChecklists(
                checklistNumber, plateNumber, customerName, startDate, endDate);
        return ResponseEntity.ok(checklists);
    }
}