package com.ashish.claimbridge.claimservice.event;

import com.ashish.claimbridge.claimservice.model.ClaimStatus;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ClaimEvent {
    private Long claimId;
    private String claimNumber;
    private Long patientId;
    private String hospitalId;
    private String insurerId;
    private ClaimStatus status;
    private Double totalAmount;
    private LocalDateTime timestamp;
    private String message;

}
