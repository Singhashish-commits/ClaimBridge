package com.ashish.claimbridge.claimservice.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDate;
import java.util.List;

@Data
@AllArgsConstructor


public class PrescriptionDto {
    private Long patientId;
    private String doctorName;
    private LocalDate issuedDate;
    private List<PrescriptionItemDto> items;
}
