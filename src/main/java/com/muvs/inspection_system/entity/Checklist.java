package com.muvs.inspection_system.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "checklists")
public class Checklist {
    
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id", updatable = false, nullable = false)
    private UUID id;
    
    @Column(name = "checklist_number", nullable = false, unique = true, length = 50)
    private String checklistNumber;
    
    @Column(name = "rental_start_date", nullable = false)
    private LocalDate rentalStartDate;
    
    @Column(name = "rental_end_date")
    private LocalDate rentalEndDate;
    
    @Column(name = "customer_name", nullable = false, length = 200)
    private String customerName;
    
    @Column(name = "customer_phone", nullable = false, length = 20)
    private String customerPhone;
    
    @Column(name = "staff_name", nullable = false, length = 200)
    private String staffName;
    
    @Column(name = "rental_type", nullable = false, length = 20)
    private String rentalType;
    
    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "vehicle_id", nullable = false)
    @JsonIgnore
    private Vehicle vehicle;
    
    @JsonIgnore
    @OneToMany(mappedBy = "checklist", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<SubChecklist> subChecklists = new ArrayList<>();
    
    @JsonIgnore
    @OneToMany(mappedBy = "checklist", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<Defect> defects = new ArrayList<>();
    
    @JsonIgnore
    @OneToMany(mappedBy = "checklist", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<VehicleChangeLog> vehicleChangeLogs = new ArrayList<>();
    
    @JsonIgnore
    @OneToMany(mappedBy = "checklist", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<Signature> signatures = new ArrayList<>();
}