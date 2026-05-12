package com.ashish.claimbridgeauthservice.model;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Data
public class Organization {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false)
    private String name;

    @Column(unique = true, nullable = false)
    private String tenantId;

    @Enumerated(EnumType.STRING)
    private Role orgType;
}
