package com.ashish.claimbridge.prescriptionservice.dto;

import lombok.Data;

import java.time.LocalDate;
@Data
public class PatientDto {
    private Long Id;
    private String firstName;
    private String lastName;
    private String tenantId;

}
