package com.ashish.claimbridge.patientservice.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class InsurerDto {
    private  String insurerName;
    private  String insurerEmail;
    private  String  insurerId;
}
