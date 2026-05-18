package com.ashish.claimbridge.prescriptionservice.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PrescriptionItemDto {
    private String drugCode;
    private String drugName;
    private String dosage;
    private String quantity;
    private LocalDateTime expiryDate;
}
