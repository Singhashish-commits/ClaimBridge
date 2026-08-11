package com.ashish.claimbridge.claimservice.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor


public class PrescriptionDto {
    private Long patientId;
    private String doctorName;
    private LocalDate issuedDate;
    private String prescriptionStatus;
    private List<PrescriptionItemDto> items;
}
