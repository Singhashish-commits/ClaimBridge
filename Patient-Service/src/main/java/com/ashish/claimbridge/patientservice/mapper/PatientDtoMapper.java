package com.ashish.claimbridge.patientservice.mapper;

import com.ashish.claimbridge.patientservice.dto.PatientDto;
import com.ashish.claimbridge.patientservice.model.Patient;

public class PatientDtoMapper {
    static public PatientDto mapPatientEntityToPatientDto(Patient patient) {
        PatientDto patientDto = new PatientDto();
        patientDto.setFirstName(patient.getFirstName());
        patientDto.setLastName(patient.getLastName());
        patientDto.setEmail(patient.getEmail());
        patientDto.setGender((patient.getGender().name().toUpperCase()));
        patientDto.setDateOfBirth(patient.getDateOfBirth());
        patientDto.setMobileNumber(patient.getMobileNumber());
        patientDto.setInsuranceId(patient.getInsuranceId());
        patientDto.setInsuranceProvider(patient.getInsuranceProvider());
        patientDto.setAddress(patient.getAddress());
        patientDto.setCity(patient.getCity());
        patientDto.setState(patient.getState());
        patientDto.setAadhaarId(patient.getAadhaarId());
        patientDto.setId(patient.getId());
        patientDto.setHospitalId(patient.getTenantId());
        return patientDto;
    }
}
