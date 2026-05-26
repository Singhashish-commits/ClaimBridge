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
public class DuplicateClaimRule implements FraudRule {
    private final FraudRepository fraudRepository;
            @Autowired
            public DuplicateClaimRule(FraudRepository fraudRepository){
                this.fraudRepository=fraudRepository;
            }

    @Override
    public FraudRuleResult evaluate(ClaimSubmittedEvent event){
                boolean duplicate = fraudRepository.existsByPatientIdAndPrescriptionIdAndEvaluationDateAfter(
                        event.getPatientId(),event.getPrescriptionId(), LocalDateTime.now().minusDays(30));
                if(duplicate){
                    return new FraudRuleResult(FraudResult.FLAGGED,
                            "Duplicate Claim Exists","same Prescription Claimed within 30 days");
                }
                return new FraudRuleResult(FraudResult.PASS,"Duplicate Claim Rule",null);
    }

}
