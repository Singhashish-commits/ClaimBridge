package com.ashish.claimbridgeauthservice.Dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class RegisterOrganizationDto {
    private String organizationType;

    private String organizationName;

    private String address;

    private String city;

    private String state;

    private String zipCode;

    private String phone;

    private String adminName;

    private String adminEmail;

    private String password;

}
