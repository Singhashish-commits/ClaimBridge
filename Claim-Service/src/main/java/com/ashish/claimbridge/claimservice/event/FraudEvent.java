package com.ashish.claimbridge.claimservice.event;

import lombok.Data;

@Data

public class FraudEvent {
    private long claimId;
    private long patentId;
    private String tenantId;
    private double totalClaimAmount;
}

