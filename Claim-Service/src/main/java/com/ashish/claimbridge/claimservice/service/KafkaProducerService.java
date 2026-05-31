package com.ashish.claimbridge.claimservice.service;

import com.ashish.claimbridge.claimservice.dto.InsurancePolicyDto;
import com.ashish.claimbridge.claimservice.event.ClaimEvent;
import com.ashish.claimbridge.claimservice.event.FraudEvent;
import com.ashish.claimbridge.claimservice.feignClient.PatientClient;
import com.ashish.claimbridge.claimservice.model.Claim;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor

public class KafkaProducerService {
    private final PatientClient patientClient;
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
    public void sendClaimSubmittedEvent(Claim claim){
        ClaimEvent event = buildEvent(claim,"claim-Submitted");
        kafkaTemplate.send("claim-submitted",event);

    }

    public void sendClaimApprovedEvent(Claim claim){
        ClaimEvent event = buildEvent(claim,"claim-Approved");
        kafkaTemplate.send("claim-approved",event);
    }
    public void sendClaimRejectedEvent(Claim claim){
        ClaimEvent event = buildEvent(claim,"claim-Rejected");
        kafkaTemplate.send("claim-rejected",event);
    }
    public void sendClaimCancelledEvent(Claim claim){
        ClaimEvent event = buildEvent(claim,"claim-Cancelled");
        kafkaTemplate.send("claim-cancelled",event);
    }



    public ClaimEvent buildEvent(Claim claim, String message) {
        ClaimEvent event = new ClaimEvent();
        event.setClaimId(claim.getId());
        event.setClaimNumber(claim.getClaimNumber());
        event.setPatientId(claim.getPatientId());
        event.setHospitalId(claim.getHospitalId());
        event.setInsurerId(claim.getInsurerId());
        event.setStatus(claim.getStatus());
        event.setTotalClaimAmount(claim.getTotalClaimAmount());
        event.setTimestamp(LocalDateTime.now());
        event.setMessage(message);
        event.setPrescriptionId(claim.getPrescriptionId());
        event.setPrescriptionStatus(claim.getPrescriptionStatus());
        event.setApprovedAmount(claim.getApprovedAmount());
        event.setInsurancePolicyId(claim.getInsurancePolicyId());
        ResponseEntity<InsurancePolicyDto> insurancePolicyDto = patientClient.findByIdAndPatientId(claim.getInsurancePolicyId(),
                claim.getPatientId(), "SYSTEM_INTERNAL");
        InsurancePolicyDto dto = insurancePolicyDto.getBody();
        event.setCoverageLimit(dto.getCoverageLimit());
        event.setUsedAmount(dto.getUsedAmount());
        event.setClaimAmount(claim.getTotalClaimAmount());




        return event;

    }


}
