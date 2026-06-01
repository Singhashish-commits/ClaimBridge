package com.ashish.claimbridge.notificationservice.event;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ClaimEvent {
    private Long claimId;
    private String claimNumber;
    private Long patientId;
    private String hospitalId;
    private String insurerId;
    private String  status;
    private Double totalClaimAmount;
    private Double approvedAmount;
    private LocalDateTime timestamp;
    private String message;
    private Long prescriptionId;
    private String prescriptionStatus;
    private Long insurancePolicyId;
    private Double coverageLimit;
    private Double usedAmount;
    private Double claimAmount;

}
