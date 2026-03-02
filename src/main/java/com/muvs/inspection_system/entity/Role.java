package com.muvs.inspection_system.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "roles")
public class Role {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(length = 20, unique = true, nullable = false)
    private String name;

    @Column(nullable = false, columnDefinition = "integer default 1")
    private Integer level;  // 1=USER, 2=INSPECTOR, 3=MANAGER, 4=ADMIN, 5=SUPERADMIN

    @Column(length = 255)
    private String description;
}
