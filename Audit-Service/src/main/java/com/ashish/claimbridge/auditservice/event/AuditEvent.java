package com.ashish.claimbridge.auditservice.event;

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
public class AuditEvent {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    private String eventType;
    private String topic;
    private Long claimId;
    private Long patientId;
    private String hospitalId;
    private String insurerId;
    private String payload;
    private LocalDateTime receivedAt;


}
