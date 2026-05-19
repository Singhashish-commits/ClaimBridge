package com.ashish.claimbridge.claimservice.dto;

import com.ashish.claimbridge.claimservice.model.BillItemCategory;
import lombok.Data;

@Data
public class ClaimItemSubmissionDto {
    private String itemName;
    private BillItemCategory billItemCategory;
    private Integer quantity;
    private Double price;
}
