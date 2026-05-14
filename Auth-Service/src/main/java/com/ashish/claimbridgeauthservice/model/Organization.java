package com.ashish.claimbridgeauthservice.model;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Data
public class Organization {
    @Id
    private String tenantId;
    private String name;
    private String orgType;
}
