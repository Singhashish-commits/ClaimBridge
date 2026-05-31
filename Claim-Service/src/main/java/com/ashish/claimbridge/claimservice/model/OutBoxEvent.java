package com.ashish.claimbridge.claimservice.model;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
public class OutBoxEvent {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private String id;
    private String eventType; // Claim Approved , Rejected
    private String payload;// Json String ;
    private String topic;
    private boolean published;
    private LocalDateTime createdAt;
    private LocalDateTime publishedAt;


}
