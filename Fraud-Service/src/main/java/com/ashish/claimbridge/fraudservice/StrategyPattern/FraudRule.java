package com.ashish.claimbridge.fraudservice.StrategyPattern;

import com.ashish.claimbridge.fraudservice.event.ClaimSubmittedEvent;
import com.ashish.claimbridge.fraudservice.model.FraudRuleResult;
import org.springframework.stereotype.Component;

@Component
public interface FraudRule {
    FraudRuleResult evaluate(ClaimSubmittedEvent event);
}
