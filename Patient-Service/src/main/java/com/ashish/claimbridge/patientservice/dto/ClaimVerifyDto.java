package com.ashish.claimbridge.patientservice.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ClaimVerifyDto {
    String aadhaarId;
    String insuranceId;
    String tenantId;
    String role;
    Long patientId;
}
