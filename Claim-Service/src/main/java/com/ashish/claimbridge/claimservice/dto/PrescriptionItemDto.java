package com.ashish.claimbridge.claimservice.dto;

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
    private String quantity;
    private LocalDateTime expiryDate;
}
