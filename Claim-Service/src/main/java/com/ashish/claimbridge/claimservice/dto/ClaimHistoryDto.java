package com.ashish.claimbridge.claimservice.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ClaimHistoryDto {
    private Long claimId;
    private String fromState;
    private String toState;
    private String triggeredBy;
    private String notes;
    private LocalDateTime timestamp;
}

