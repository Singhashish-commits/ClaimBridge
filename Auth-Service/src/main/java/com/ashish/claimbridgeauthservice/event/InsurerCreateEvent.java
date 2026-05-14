package com.ashish.claimbridgeauthservice.event;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class InsurerCreateEvent {

    private String tenantId;

    private String insurerName;

    private String address;

    private String city;

    private String state;

    private String zipCode;

    private String phone;
    private String adminName;

    private String adminEmail;

    private String password;

}
