package com.muvs.inspection_system.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.muvs.inspection_system.enums.DefectType;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "defects")
public class Defect {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "defect_id")
    private Long defectId;
    
        @ManyToOne(fetch = FetchType.LAZY)
        @JoinColumn(name = "checklist_id")
        private Checklist checklist;
    
            @ManyToOne(fetch = FetchType.LAZY)
            @JoinColumn(name = "sub_checklist_id")
            private SubChecklist subChecklist;
        
            @Column(name = "item_id")
            private Long itemId;    @Enumerated(EnumType.STRING)
    @Column(name = "defect_type", nullable = false, length = 50)
    private DefectType defectType;
    
    @Column(name = "description", length = 500)
    private String description;
    
    @Column(name = "diagram_x")
    private Integer diagramX;
    
    @Column(name = "diagram_y")
    private Integer diagramY;
    
    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;
    
    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
    
    @JsonIgnore
    @OneToMany(mappedBy = "defect", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<DefectImage> images = new ArrayList<>();
}