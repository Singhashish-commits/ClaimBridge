package com.ashish.claimbridge.patientservice.dto;

import com.ashish.claimbridge.patientservice.model.PolicyStatus;
import lombok.Data;

import java.time.LocalDate;

@Data
public class InsurancePolicyDto {
    private Long patientId;
    private String insurerId;
    private String policyNumber;
    private Double coverageLimit;
    private Double usedAmount;
    private LocalDate validFrom;
    private LocalDate validTo;
    private PolicyStatus status;
}
