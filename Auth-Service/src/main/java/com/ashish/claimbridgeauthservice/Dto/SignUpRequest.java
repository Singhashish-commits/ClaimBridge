package com.ashish.claimbridgeauthservice.Dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class SignUpRequest {
    private String name;
    private String email;
    private String password;
    private String role; // e.g., "HOSPITAL" or "INSURER"
    private String organizationName; //

}
