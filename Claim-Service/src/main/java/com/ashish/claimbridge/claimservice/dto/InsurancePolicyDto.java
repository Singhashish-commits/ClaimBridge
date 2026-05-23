package com.ashish.claimbridge.claimservice.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class InsurancePolicyDto {
    private Long patientId;
    private String insurerId;
    private String policyNumber;
    private Double coverageLimit;
    private Double usedAmount;
    private LocalDate validFrom;
    private LocalDate validTo;
    private String policyStatus;
}
