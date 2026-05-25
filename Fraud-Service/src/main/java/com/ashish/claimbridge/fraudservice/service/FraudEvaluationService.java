package com.ashish.claimbridge.fraudservice.service;

import com.ashish.claimbridge.fraudservice.StrategyPattern.FraudRule;
import com.ashish.claimbridge.fraudservice.event.ClaimSubmittedEvent;
import com.ashish.claimbridge.fraudservice.model.FraudEvaluationRecord;
import com.ashish.claimbridge.fraudservice.model.FraudResult;
import com.ashish.claimbridge.fraudservice.model.FraudRuleResult;
import com.ashish.claimbridge.fraudservice.repository.FraudRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class FraudEvaluationService {
    private final List<FraudRule>fraudRules;
    private final FraudRepository fraudRepository;
    private final KafkaTemplate<String, Object> kafkaTemplate;
    @Autowired
    public FraudEvaluationService(List<FraudRule> fraudRules,FraudRepository fraudRepository,KafkaTemplate<String, Object> kafkaTemplate) {
        this.fraudRules=fraudRules;
        this.fraudRepository=fraudRepository;
        this.kafkaTemplate=kafkaTemplate;
    }

    public void evaluate(ClaimSubmittedEvent event){
        List<FraudRuleResult> results= fraudRules.stream()
                .map(rule-> rule.evaluate(event)).toList();
        boolean hasReject = results.stream().anyMatch(r-> r.getResult()== FraudResult.REJECT);

        boolean hasFlag = results.stream().anyMatch(r-> r.getResult()== FraudResult.FLAGGED);

        FraudResult finalResult = hasReject ? FraudResult.REJECT
                                :hasFlag ? FraudResult.FLAGGED
                                :FraudResult.PASS;
        String flaggedRules = results.stream()
                .filter(r->r.getResult()!= FraudResult.PASS)
                .map(FraudRuleResult ::getRuleName)
                .collect(Collectors.joining(" ,"));

        FraudEvaluationRecord record= new FraudEvaluationRecord();
        record.setEvaluationDate(LocalDateTime.now());
        record.setFraudResult(finalResult);
        record.setHospitalId(event.getHospitalId());
        record.setPatientId(event.getPatientId());
        record.setClaimId(event.getClaimId());
        record.setTotalClaimAmount(event.getClaimAmount());
        record.setReason(flaggedRules);
        record.setInsurerId(event.getInsurerId());
        record.setPrescriptionId(event.getPrescriptionId());
        record.setFlaggedRule(flaggedRules);

        fraudRepository.save(record);

        if(finalResult!= FraudResult.PASS){

        }




    }
}
