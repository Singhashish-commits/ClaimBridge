package com.ashish.claimbridge.notificationservice.service;

import com.ashish.claimbridge.notificationservice.event.ClaimEvent;
import com.ashish.claimbridge.notificationservice.model.Notification;
import com.ashish.claimbridge.notificationservice.repository.NotificationRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class NotificationService {
    private final NotificationRepository notificationRepository;
    @Autowired
    public NotificationService(NotificationRepository notificationRepository) {
        this.notificationRepository = notificationRepository;
    }

    public void SendNotification(ClaimEvent event) {
        String message = switch (event.getStatus()){
            case "APPROVED" -> "Claim #"+event.getClaimId()+
            "Approved Amount: "+event.getApprovedAmount()+" Claim Amount: "+event.getClaimAmount();
            case "PARTIALLY_APPROVED" -> "Claim #" + event.getClaimId() +
                    " partially approved! Amount: " + event.getApprovedAmount();
            case "REJECTED" -> "Claim #" + event.getClaimId() +
                    " rejected! Reason: " + event.getMessage();
            case "CANCELLED" -> "Claim #" + event.getClaimId() +
                    " cancelled!";
            default -> "Claim #" + event.getClaimId() + " status updated!";
        };









        Notification notification = new Notification();
        notification.setClaimId(event.getClaimId());
        notification.setMessage(message);

        notification.setType("Email");
        notification.setStatus("Sent");
        notification.setSentAt(LocalDateTime.now());
        notificationRepository.save(notification);
        System.out.println("notification sent for teh claim " +event.getClaimId());


    }






}
