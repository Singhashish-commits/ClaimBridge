package com.ashish.claimbridge.claimservice.event;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class FraudFlaggedEvent {
    private Long claimId;
    private String result;
    private String reason;
}
