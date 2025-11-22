package com.muvs.inspection_system.repository;

import com.muvs.inspection_system.entity.Checklist;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface ChecklistRepository extends JpaRepository<Checklist, UUID>, JpaSpecificationExecutor<Checklist> {
    
    Optional<Checklist> findByChecklistNumber(String checklistNumber);
    
    boolean existsByChecklistNumber(String checklistNumber);
}