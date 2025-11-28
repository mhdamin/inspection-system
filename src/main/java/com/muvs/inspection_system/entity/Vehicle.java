package com.muvs.inspection_system.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = false)
@Entity
@Table(name = "vehicles")
public class Vehicle extends BaseEntity {
    
    @Column(name = "plate_number", nullable = false, unique = true, length = 50)
    private String plateNumber;
    
    @Column(name = "model", nullable = false, length = 100)
    private String model;
    
    @Column(name = "manufacturer", nullable = false, length = 100)
    private String manufacturer;
    
    @Column(name = "`year`", nullable = false)
    private Integer year;
    
    @Column(name = "status", nullable = false, length = 50)
    private String status;
    
    @JsonIgnore
    @OneToMany(mappedBy = "vehicle", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<Checklist> checklists = new ArrayList<>();
}