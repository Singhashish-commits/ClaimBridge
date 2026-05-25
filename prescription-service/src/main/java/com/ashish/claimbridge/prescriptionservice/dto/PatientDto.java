package com.ashish.claimbridge.prescriptionservice.dto;

import lombok.Data;

@Data
public class PatientDto {
    private Long Id;
    private String firstName;
    private String lastName;
    private String tenantId;
    private String insurerId;

}
