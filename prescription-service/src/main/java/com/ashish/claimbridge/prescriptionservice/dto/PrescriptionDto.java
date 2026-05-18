package com.ashish.claimbridge.prescriptionservice.dto;

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
    private List<PrescriptionItemDto> items;

}
