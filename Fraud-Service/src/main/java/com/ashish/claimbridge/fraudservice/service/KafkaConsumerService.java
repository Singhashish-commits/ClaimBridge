package com.ashish.claimbridge.fraudservice.service;

import com.ashish.claimbridge.fraudservice.event.ClaimSubmittedEvent;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;
import tools.jackson.databind.ObjectMapper;

@Service
@RequiredArgsConstructor
public class KafkaConsumerService {
    private final FraudEvaluationService fraudEvaluationService;

    @KafkaListener(topics = "claim-submitted",groupId = "fraud-service-group")
    public void consumeClaimSubmitted(String claimSubmitted) {
        System.out.println("Claim submitted: " + claimSubmitted);
        try{
            ObjectMapper objectMapper = new ObjectMapper();
            ClaimSubmittedEvent event = objectMapper.readValue(claimSubmitted, ClaimSubmittedEvent.class);

        }catch(Exception e){
            e.printStackTrace();
        }
    }
}
