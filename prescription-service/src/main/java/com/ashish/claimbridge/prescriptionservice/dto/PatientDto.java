package com.ashish.claimbridge.prescriptionservice.dto;

import lombok.Data;

@Data
public class PatientDto {
    private Long id;
    private String firstName;
    private String lastName;
    private String hospitalId;
    private String insuranceId;

}
