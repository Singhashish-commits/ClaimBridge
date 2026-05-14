package com.ashish.claimbridgeauthservice.Dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CreateUser {
    private String name;
    private String email;
    private String password;
}
