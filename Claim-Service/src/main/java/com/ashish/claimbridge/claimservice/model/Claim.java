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
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String claimNumber;
    private Long patientId;
    private Long insurancePolicyId;
    private Double totalClaimAmount;
    private Double approvedAmount;
    private Double rejectedAmount;
    @Enumerated(EnumType.STRING)
    private ClaimStatus status;
    private String diagnosis;
    private String remarks;
    private LocalDateTime submittedAt;
    @OneToMany(mappedBy = "claim",
            cascade = CascadeType.ALL,
            orphanRemoval = true)
    private List<ClaimItem> claimItems;
    private String hospitalId;
    private String insurerId;
    private Long prescriptionId;


    public void addClaimItem(ClaimItem item) {
        if (claimItems == null) {
            this.claimItems = new ArrayList<>();
        }
        this.claimItems.add(item);
        item.setClaim(this);
    }


}
