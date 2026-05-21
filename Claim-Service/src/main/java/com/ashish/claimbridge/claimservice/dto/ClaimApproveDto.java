package com.ashish.claimbridge.claimservice.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ClaimApproveDto {
    private double approvedAmount;
    private String remark;
    private List<ClaimItemApproveDto> itemApprovals;
}
