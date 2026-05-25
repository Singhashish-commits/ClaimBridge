package com.ashish.claimbridge.fraudservice.fraudRules;

import com.ashish.claimbridge.fraudservice.StrategyPattern.FraudRule;
import com.ashish.claimbridge.fraudservice.event.ClaimSubmittedEvent;
import com.ashish.claimbridge.fraudservice.model.FraudResult;
import com.ashish.claimbridge.fraudservice.model.FraudRuleResult;
import com.ashish.claimbridge.fraudservice.repository.FraudRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;

@Configuration
public class PolicyCoverageRule implements FraudRule {
    private final FraudRepository fraudRepository;
    @Autowired
    public PolicyCoverageRule(FraudRepository fraudRepository){
        this.fraudRepository=fraudRepository;
    }


    @Override
    public FraudRuleResult evaluate(ClaimSubmittedEvent event) {
        double coverageLimit = event.getCoverageLimit();
        double usedAmount = event.getUsedAmount();
        double remainingCoverageLimit = coverageLimit - usedAmount;
        if(event.getClaimAmount()> remainingCoverageLimit){
            return new FraudRuleResult(
                    FraudResult.FLAGGED,
                    "ExcessAmountRule",
                    "Claim amount exceeds remaining coverage limit");
        }
        return new FraudRuleResult(FraudResult.PASS, "ExcessiveAmountRule", null);
    }
}
