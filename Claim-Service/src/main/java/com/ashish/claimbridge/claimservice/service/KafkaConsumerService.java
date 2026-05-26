package com.ashish.claimbridge.claimservice.service;

import com.ashish.claimbridge.claimservice.event.FraudFlaggedEvent;
import com.ashish.claimbridge.claimservice.model.Claim;
import com.ashish.claimbridge.claimservice.model.ClaimStatus;
import com.ashish.claimbridge.claimservice.repository.ClaimRepository;
import tools.jackson.databind.ObjectMapper;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Service
public class KafkaConsumerService {

    private final ClaimRepository claimRepository;

    public KafkaConsumerService(ClaimRepository claimRepository) {
        this.claimRepository = claimRepository;
    }

    @KafkaListener(topics = "fraud-flagged",groupId = "claim-service-group")
    public void consumeFraudFlag(String message){
        ObjectMapper mapper = new ObjectMapper();
        FraudFlaggedEvent event = mapper.readValue(message, FraudFlaggedEvent.class);
        Claim claim = claimRepository.findById(event.getClaimId())
                .orElseThrow(()->new RuntimeException("Claim not found"));
        if(event.getResult().equals("REJECTED")){
            claim.setStatus(ClaimStatus.REJECTED);
            claim.setRemarks(event.getReason());
        }
        else{
            claim.setStatus(ClaimStatus.FLAGGED);
            claim.setRemarks(event.getReason());
        }
        claimRepository.save(claim);
    }
}
