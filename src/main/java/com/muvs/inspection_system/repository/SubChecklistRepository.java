package com.muvs.inspection_system.repository;

import com.muvs.inspection_system.entity.SubChecklist;
import com.muvs.inspection_system.enums.SubChecklistType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface SubChecklistRepository extends JpaRepository<SubChecklist, UUID> {
    
    List<SubChecklist> findByChecklistIdOrderByTimestampAsc(UUID checklistId);
    
    Optional<SubChecklist> findBySubChecklistNumber(String subChecklistNumber);
    
    boolean existsBySubChecklistNumber(String subChecklistNumber);
    
    List<SubChecklist> findByType(SubChecklistType type);
    
    List<SubChecklist> findByVehicleId(UUID vehicleId);
}