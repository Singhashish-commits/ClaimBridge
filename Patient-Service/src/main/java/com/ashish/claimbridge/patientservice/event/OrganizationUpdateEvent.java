package com.ashish.claimbridge.patientservice.event;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class OrganizationUpdateEvent {
    private String address;
    private String city;
    private String phone ;
    private String state;
    private String zipCode;
    private String type;
    private String tenantId;
}
