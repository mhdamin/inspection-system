package com.muvs.inspection_system.repository;

import com.muvs.inspection_system.entity.VehicleChangeLog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface VehicleChangeLogRepository extends JpaRepository<VehicleChangeLog, Integer> {
    List<VehicleChangeLog> findTop10ByOrderByTimestampDesc();

    // Filter by staff name
    List<VehicleChangeLog> findByStaffNameContainingIgnoreCaseOrderByTimestampDesc(String staffName);

    // Filter by change type
    List<VehicleChangeLog> findByChangeTypeOrderByTimestampDesc(String changeType);

    // Filter by date range
    List<VehicleChangeLog> findByTimestampBetweenOrderByTimestampDesc(LocalDateTime startDate, LocalDateTime endDate);

    // Custom query for combined filtering
    @Query("SELECT v FROM VehicleChangeLog v WHERE " +
           "(:staffName IS NULL OR LOWER(v.staffName) LIKE LOWER(CONCAT('%', :staffName, '%'))) AND " +
           "(:changeType IS NULL OR v.changeType = :changeType) AND " +
           "(:startDate IS NULL OR v.timestamp >= :startDate) AND " +
           "(:endDate IS NULL OR v.timestamp <= :endDate) " +
           "ORDER BY v.timestamp DESC")
    List<VehicleChangeLog> findByFilters(
            @Param("staffName") String staffName,
            @Param("changeType") String changeType,
            @Param("startDate") LocalDateTime startDate,
            @Param("endDate") LocalDateTime endDate
    );
}
