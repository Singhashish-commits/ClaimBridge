package com.ashish.claimbridge.fraudservice.StrategyPattern;

import com.ashish.claimbridge.fraudservice.model.FraudRuleResult;

public interface FraudRule {
    FraudRuleResult evaluate(ClaimSubmittedEvent event)
}
