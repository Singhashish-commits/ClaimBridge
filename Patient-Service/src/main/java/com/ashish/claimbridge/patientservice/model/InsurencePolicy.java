package com.ashish.claimbridge.patientservice.model;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDate;

@Data
@Entity
public class InsurencePolicy {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String insurerId; // link to insurer id;
    private Long patientId; // link patient id
    private String policyNumber;
    private Double coverageLimit;
    private Double usedAmount;
    private LocalDate validFrom;
    private LocalDate validTo;
    @Enumerated(EnumType.STRING)
    private PolicyStatus status; // ACTIVE, INACTIVE, EXPIRED
    private String tenantId;

}

