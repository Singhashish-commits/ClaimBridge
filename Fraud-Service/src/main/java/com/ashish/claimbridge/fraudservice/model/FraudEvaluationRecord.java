package com.ashish.claimbridge.fraudservice.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
public class FraudEvaluationRecord {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private long claimId;
    private long patientId;
    private long hospitalId;
    private long prescriptionId;
    private double totalClaimAmount;
    @Enumerated(EnumType.STRING)
    private FraudResult fraudResult;

    private String flaggedRule;
    private String reason;
    private LocalDateTime evaluationDate;



}
