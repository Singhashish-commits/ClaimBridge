package com.ashish.claimbridge.prescriptionservice.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class DrugDto {
    private String drugCode;
    private String drugName;
    private String category;

}
