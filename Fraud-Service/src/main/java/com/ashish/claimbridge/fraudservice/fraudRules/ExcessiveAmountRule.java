package com.ashish.claimbridge.fraudservice.fraudRules;

import com.ashish.claimbridge.fraudservice.StrategyPattern.FraudRule;
import com.ashish.claimbridge.fraudservice.event.ClaimSubmittedEvent;
import com.ashish.claimbridge.fraudservice.model.FraudRuleResult;

public class ExcessiveAmountRule implements FraudRule {



    @Override
    public FraudRuleResult evaluate(ClaimSubmittedEvent event) {

        return null;
    }
}
