package com.ashish.claimbridge.prescriptionservice.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PrescriptionItemDto {
    private String drugCode;
    private String drugName;
    private String dosage;
    private Double quantity;
    private LocalDateTime expiryDate;
    private Double billedAmount;
    private Double standardCost; // will be used when the fraud service need this
}
