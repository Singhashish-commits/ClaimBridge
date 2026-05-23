package com.ashish.claimbridge.fraudservice.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class FraudRuleResult {
    private FraudResult result;
    private String ruleName;
    private String reason;

}
