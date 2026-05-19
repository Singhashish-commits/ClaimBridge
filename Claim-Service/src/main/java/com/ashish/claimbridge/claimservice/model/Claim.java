package com.ashish.claimbridge.claimservice.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Claim {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private String id;
    private String claimNumber;

    private Long patientId;

    private Long insurancePolicyId;

    private Double totalClaimAmount;

    private Double approvedAmount;

    private Double rejectedAmount;

    @Enumerated(EnumType.STRING)
    private ClaimType claimType;

    @Enumerated(EnumType.STRING)
    private ClaimStatus status;

    private String diagnosis;

    private String remarks;

    private LocalDateTime submittedAt;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "hospital_bill_id")
    private HospitalBill hospitalBill;

    @OneToMany(mappedBy = "claim",
            cascade = CascadeType.ALL,
            orphanRemoval = true)
    private List<ClaimItem> claimItems;



}
