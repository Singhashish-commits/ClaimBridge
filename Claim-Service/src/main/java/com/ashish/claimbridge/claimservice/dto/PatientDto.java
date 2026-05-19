package com.ashish.claimbridge.claimservice.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PatientDto {
    private Long Id;
    private String firstName;
    private String lastName;
    private String tenantId;
}
