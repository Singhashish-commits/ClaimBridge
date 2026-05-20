package com.ashish.claimbridge.claimservice.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ClaimItemApproveDto {
    private Long claimItemId;
    private Double approvedAmount;
    private String rejectionReason;
}
