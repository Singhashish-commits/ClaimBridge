package com.ashish.claimbridge.fraudservice.fraudRules;

import com.ashish.claimbridge.fraudservice.StrategyPattern.FraudRule;
import com.ashish.claimbridge.fraudservice.event.ClaimSubmittedEvent;
import com.ashish.claimbridge.fraudservice.model.FraudResult;
import com.ashish.claimbridge.fraudservice.model.FraudRuleResult;

public class ExpiredPrescriptionRule  implements FraudRule {

    @Override
    public FraudRuleResult evaluate(ClaimSubmittedEvent event) {
        if("EXPIRED".equals(event.getPrescriptionStatus())){
            return new FraudRuleResult(FraudResult.REJECT, "ExpiredPrescriptionRule", "rejected");
        }
        return new FraudRuleResult(FraudResult.PASS, "Expired Prescription Rule", "passed");
    }
}
