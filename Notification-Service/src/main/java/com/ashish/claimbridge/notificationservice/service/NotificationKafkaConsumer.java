package com.ashish.claimbridge.notificationservice.service;

import com.ashish.claimbridge.notificationservice.event.ClaimEvent;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;
import tools.jackson.databind.ObjectMapper;

@Service
public class NotificationKafkaConsumer {
    private final NotificationService notificationService;
    private final ObjectMapper objectMapper;
    @Autowired
    public NotificationKafkaConsumer(NotificationService notificationService, ObjectMapper objectMapper) {
        this.notificationService = notificationService;
        this.objectMapper = objectMapper;
    }

    @KafkaListener(
            topics = {"claim.approved", "claim.rejected", "claim.cancelled","claim.partially-approved"},
            groupId = "notification-service-group"
    )
    public void consumeClaimEvents(String message){
        try{

            ClaimEvent claimEvent = objectMapper.readValue(message,ClaimEvent.class);
            notificationService.SendNotification(claimEvent);
        }catch (Exception e){
            System.out.println("Error: " + e.getMessage());
        }
    }


}
