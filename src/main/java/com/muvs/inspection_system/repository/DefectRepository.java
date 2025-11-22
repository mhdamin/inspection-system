package com.muvs.inspection_system.repository;

import com.muvs.inspection_system.entity.Defect;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface DefectRepository extends JpaRepository<Defect, Long> {
    
    List<Defect> findByChecklist_IdOrderByCreatedAtDesc(UUID checklistId);
    
    List<Defect> findByItemId(Long itemId);
    
    boolean existsByChecklist_Id(UUID checklistId);
}