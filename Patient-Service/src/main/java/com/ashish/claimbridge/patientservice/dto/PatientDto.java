package com.ashish.claimbridge.patientservice.dto;

import com.ashish.claimbridge.patientservice.model.Gender;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PatientDto {
    private String firstName;
    private String lastName;
    private LocalDate dateOfBirth;
    @Enumerated(EnumType.STRING)
    private Gender gender;
    private String mobileNumber;
    private String email;
    private String insuranceId;
    private String insuranceProvider;
    private String AadhaarId;
    private String address;
    private String city;
    private String state;

}
