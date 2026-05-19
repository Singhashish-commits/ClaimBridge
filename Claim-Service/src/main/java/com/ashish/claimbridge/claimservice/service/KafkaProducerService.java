package com.ashish.claimbridge.claimservice.service;

import com.ashish.claimbridge.claimservice.event.FraudEvent;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor

public class KafkaProducerService {
    private final KafkaTemplate<String, Object> kafkaTemplate;
    public void sendFraudCheckEvent(Long claimId, Long patientId,Double amount, String tenantId){
        FraudEvent event = new FraudEvent();
        event.setClaimId(claimId);
        event.setTenantId(tenantId);
        event.setPatentId(patientId);
        event.setTotalClaimAmount(amount);
        kafkaTemplate.send("claim-fraud-check",event);
        System.out.println("Fraud check event sent for claim: " + claimId);
    }
}
