package com.ashish.claimbridge.patientservice.model;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.Data;

@Entity
@Data
public class Hospital {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id; // tenantId;
    private String tenantId;
    private String hospitalName;
    private String hospitalAddress;
    private String hospitalCity;
    private String hospitalState;
    private String email;
    private String phoneNumber;
    private String hospitalZip;

}
