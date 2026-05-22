package com.ashish.claimbridge.claimservice.dto;

import com.ashish.claimbridge.claimservice.model.Claim;
import com.ashish.claimbridge.claimservice.model.ClaimStatus;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;


public interface ClaimStatsSummary {
    ClaimStatus getStatus();
    Long getCount();
    Double getTotalClaimed();
    Double getTotalApproved();
    Double getTotalRejected();




}
