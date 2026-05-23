package com.ashish.claimbridge.fraudservice.event;

import com.ashish.claimbridge.fraudservice.model.ClaimStatus;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ClaimSubmittedEvent {
    private Long claimId;
    private String claimNumber;
    private Long patientId;
    private String hospitalId;
    private String insurerId;
    private ClaimStatus status;
    private Double totalAmount;
    private Double approvedAmount;
    private Double claimAmount;
    private LocalDateTime timestamp;
    private String message;
    private Long prescriptionId;
    private String prescriptionStatus;
    private Long insurancePolicyId;
    private Double coverageLimit;
    private Double usedAmount;
}
