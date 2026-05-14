package com.ashish.claimbridgeauthservice.Dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UpdateProfileRequest {
    private String address;
    private String city;
    private String state;
    private String zipCode;
    private String phone;
    private String organizationName;
}
