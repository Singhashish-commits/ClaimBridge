package com.ashish.claimbridge.patientservice.model;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.Data;

@Data
@Entity
public class Insurer {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    private String tenantId;
    private String InsurerName;
    private String insurerEmail;
    private String insurerPhone;
    private String insurerAddress;
    private String insurerCity;
    private String insurerState;
    private String insurerZip;

}
