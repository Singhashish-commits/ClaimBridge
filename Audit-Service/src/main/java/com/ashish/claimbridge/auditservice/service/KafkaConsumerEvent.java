package com.ashish.claimbridge.auditservice.service;

import org.apache.kafka.common.KafkaException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.stereotype.Service;

@Service

public class KafkaConsumerEvent {

    private final AuditService auditService;
    @Autowired
    public KafkaConsumerEvent(AuditService auditService) {
        this.auditService = auditService;
    }
    @KafkaListener(
            topics = {
                    "claim.submitted",
                    "claim.approved",
                    "claim.partially-approved",
                    "claim.rejected",
                    "claim.cancelled",
                    "fraud.flagged"
            },groupId = "audit-service-group"
    )
    public void consumeAllEvent(String message, @Header(KafkaHeaders.RECEIVED_TOPIC) String topic  ) {

        try{
            auditService.saveAuditEvent(message,topic);
        }catch (Exception e){
            System.out.println("Audit error: " + e.getMessage());
            throw  new KafkaException(e.getMessage());
        }

    }

}
