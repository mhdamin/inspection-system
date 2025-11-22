package com.muvs.inspection_system.repository;

import com.muvs.inspection_system.entity.DefectImage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface DefectImageRepository extends JpaRepository<DefectImage, Long> {
    
    List<DefectImage> findByDefectIdOrderByUploadedAtAsc(Long defectId);
    
    void deleteByDefectId(Long defectId);
}