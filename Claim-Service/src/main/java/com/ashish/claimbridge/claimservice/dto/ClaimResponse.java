package com.ashish.claimbridge.claimservice.dto;

import com.ashish.claimbridge.claimservice.model.ClaimStatus;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ClaimResponse {
    private Long id;
    private String claimNumber;
    private Long patientId;
    private Long insurancePolicyId;
    private String hospitalId;
    private String insurerId;
    private Long prescriptionId;
    private ClaimStatus status;
    private String diagnosis;
    private Double totalClaimAmount;
    private Double approvedAmount;
    private Double rejectedAmount;
    private String remarks;
    private LocalDateTime submittedAt;
    private List<ClaimItemResponseDto> claimItems;

}
