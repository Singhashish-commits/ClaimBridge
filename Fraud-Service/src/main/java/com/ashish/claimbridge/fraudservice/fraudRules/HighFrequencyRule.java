package com.ashish.claimbridge.fraudservice.fraudRules;

import com.ashish.claimbridge.fraudservice.StrategyPattern.FraudRule;
import com.ashish.claimbridge.fraudservice.event.ClaimSubmittedEvent;
import com.ashish.claimbridge.fraudservice.model.FraudResult;
import com.ashish.claimbridge.fraudservice.model.FraudRuleResult;
import com.ashish.claimbridge.fraudservice.repository.FraudRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
public class HighFrequencyRule implements FraudRule {
    private final FraudRepository fraudRepository;
    @Autowired
    public HighFrequencyRule(FraudRepository fraudRepository){
        this.fraudRepository= fraudRepository;
    }





    @Override
    public FraudRuleResult evaluate(ClaimSubmittedEvent event) {
        LocalDateTime twoHourAgo = LocalDateTime.now().minusHours(2);
        int recentClaims = fraudRepository.countByHospitalIdAndPatientIdAndEvaluationDateAfter(
                event.getHospitalId(),event.getPatientId(),twoHourAgo
        );

        if(recentClaims>=3){
            return new FraudRuleResult(FraudResult.FLAGGED,"HighFrequencyProviderRule",
                    "Hospital Submitted 3 or more claims for the same Patient !!");
        }



        return new FraudRuleResult(FraudResult.PASS, "HighFrequencyProviderRule", "passed");
    }
}
