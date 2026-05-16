package com.ashish.claimbridge.patientservice.model;

import jakarta.annotation.Nullable;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "patients")
public class Patient {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(nullable = false)
    private String firstName;

    private String lastName;

    @Column(nullable = false)
    private LocalDate dateOfBirth;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private Gender gender;

    @Column(nullable = false)
    private String mobileNumber;

    @Column(nullable = false)
    private String email;
    private String tenantId;

    @Column(unique = true)
    private String insuranceId;
    private String insuranceProvider;

    @Column(nullable = false,unique = true)
    private String aadhaarId;

    private String address;
    private String city;
    private String state;

    @Column(nullable = false)
    private boolean isInsuranceVerified = false;
}
