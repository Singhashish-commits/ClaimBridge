package com.ashish.claimbridge.claimservice.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ClaimItemResponseDto {
    private String itemName;
    private Integer quantity;
    private Double totalPrice;
    private Boolean claimable;
    private Double approvedAmount;
    private Double rejectedAmount;
    private String rejectionReason;
}
