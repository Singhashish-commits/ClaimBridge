package com.ashish.claimbridge.claimservice.dto;

import com.ashish.claimbridge.claimservice.model.BillItemCategory;
import lombok.Data;

import java.util.List;

@Data
public class ClaimSubmitDto {
    private Long patientId;
    private Long insurancePolicyId;
    private Long prescriptionId;
    private String prescriptionStatus;
    private Double totalClaimAmount;
    private String diagnosis;
    private String remarks;
    private String insurerId;
    private List<ClaimItemSubmissionDto> claimItems;



}
