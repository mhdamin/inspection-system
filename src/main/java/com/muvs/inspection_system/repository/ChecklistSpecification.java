package com.muvs.inspection_system.repository;

import com.muvs.inspection_system.entity.Checklist;
import com.muvs.inspection_system.entity.Vehicle;
import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.Predicate;
import org.springframework.data.jpa.domain.Specification;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class ChecklistSpecification {
    
    public static Specification<Checklist> withFilters(
            String checklistNumber,
            String plateNumber,
            String customerName,
            LocalDate startDate,
            LocalDate endDate) {
        
        return (root, query, criteriaBuilder) -> {
            List<Predicate> predicates = new ArrayList<>();
            
            // Filter by checklist number (exact match)
            if (checklistNumber != null && !checklistNumber.trim().isEmpty()) {
                predicates.add(criteriaBuilder.equal(
                        root.get("checklistNumber"), 
                        checklistNumber
                ));
            }
            
            // Filter by customer name (contains - case insensitive)
            if (customerName != null && !customerName.trim().isEmpty()) {
                predicates.add(criteriaBuilder.like(
                        criteriaBuilder.lower(root.get("customerName")),
                        "%" + customerName.toLowerCase() + "%"
                ));
            }
            
            // Filter by vehicle plate number
            if (plateNumber != null && !plateNumber.trim().isEmpty()) {
                Join<Checklist, Vehicle> vehicleJoin = root.join("vehicle");
                predicates.add(criteriaBuilder.equal(
                        vehicleJoin.get("plateNumber"),
                        plateNumber
                ));
            }
            
            // Filter by rental start date (greater than or equal)
            if (startDate != null) {
                predicates.add(criteriaBuilder.greaterThanOrEqualTo(
                        root.get("rentalStartDate"),
                        startDate
                ));
            }
            
            // Filter by rental start date (less than or equal)
            if (endDate != null) {
                predicates.add(criteriaBuilder.lessThanOrEqualTo(
                        root.get("rentalStartDate"),
                        endDate
                ));
            }
            
            return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
        };
    }
}