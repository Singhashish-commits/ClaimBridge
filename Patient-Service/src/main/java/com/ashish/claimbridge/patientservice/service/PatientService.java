package com.ashish.claimbridge.patientservice.service;

import com.ashish.claimbridge.patientservice.dto.ApiResponse;
import com.ashish.claimbridge.patientservice.dto.PatientDto;
import com.ashish.claimbridge.patientservice.mapper.PatientDtoMapper;
import com.ashish.claimbridge.patientservice.model.Patient;
import com.ashish.claimbridge.patientservice.repository.PatientRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class PatientService {
    private final PatientRepository patientRepository;
    @Autowired
    public PatientService(PatientRepository patientRepository) {
        this.patientRepository = patientRepository;
    }

    public ResponseEntity<ApiResponse>  savePatient(PatientDto patientDto, String role, String tenantId) {
        if(!"ROLE_HOSPITAL".equals(role)){
            throw new RuntimeException("Invalid role To Add Patient");
        }
        if ((patientDto.getInsuranceId() == null && patientDto.getInsuranceProvider() != null)
                || (patientDto.getInsuranceId() != null && patientDto.getInsuranceProvider() == null)) {

            throw new RuntimeException(
                    "Both insuranceId and insuranceProvider must be provided together"
            );
        }
        Patient patient = getPatient(patientDto, tenantId);
        patientRepository.save(patient);
        return ResponseEntity.ok(new ApiResponse("Patient Saved Successfully",true));
    }

    private static Patient getPatient(PatientDto patientDto, String tenantId) {
        Patient patient = new Patient();
        patient.setFirstName(patientDto.getFirstName());
        patient.setLastName(patientDto.getLastName());
        patient.setDateOfBirth(patientDto.getDateOfBirth());
        patient.setTenantId(tenantId); // added by the hospital Staff
        patient.setGender(patientDto.getGender());
        patient.setEmail(patientDto.getEmail());
        patient.setMobileNumber(patientDto.getMobileNumber());
        patient.setInsuranceId(patientDto.getInsuranceId());
        patient.setInsuranceProvider(patientDto.getInsuranceProvider());
        return patient;
    }

    public ResponseEntity<List<PatientDto>> getPatientByTenantId(String tenantId) {

         List<Patient> patients =  patientRepository.findByTenantId(tenantId)
                 .orElseThrow(()->new RuntimeException("Patient Not Found with this TenantId"+tenantId));
        List<PatientDto> patientList =patients.stream().map(PatientDtoMapper::mapPatientEntityToPatientDto).toList();
          return new ResponseEntity<>(patientList,HttpStatus.OK);
    }

    public ResponseEntity<PatientDto> findById(Long id,String tenantId) {
       Patient patient = patientRepository.findByIdAndTenantId(id,tenantId)
               .orElseThrow(()-> new RuntimeException("Patient not Available with this Id"+id+"and tenantId"+tenantId));
       PatientDto patientDto = PatientDtoMapper.mapPatientEntityToPatientDto(patient);
       return new ResponseEntity<>(patientDto,HttpStatus.OK);
    }



    public ResponseEntity<ApiResponse> updatePatient(long id, PatientDto patientDto, String tenantId,String role) {
        if(!"ROLE_HOSPITAL".equals(role)){
            throw new RuntimeException("you are not Authorized to Update Patient Details");
        }
        Patient current = patientRepository.findByIdAndTenantId(id, tenantId)
                .orElseThrow(()-> new RuntimeException("Patient not found with id " + id+"and Hospital id " + tenantId) );
        current.setFirstName(patientDto.getFirstName());
        current.setLastName(patientDto.getLastName());
        current.setEmail(patientDto.getEmail());
        current.setGender(patientDto.getGender());
        current.setMobileNumber(patientDto.getMobileNumber());
        current.setInsuranceId(patientDto.getInsuranceId());
        current.setInsuranceProvider(patientDto.getInsuranceProvider());
        current.setDateOfBirth(patientDto.getDateOfBirth());
        current.setAddress(patientDto.getAddress());
        current.setAadhaarId(patientDto.getAadhaarId());
        current.setCity(patientDto.getCity());
        current.setState(patientDto.getState());
        patientRepository.save(current);
        return new ResponseEntity<>(new ApiResponse("Patient Updated Successfully",true),HttpStatus.OK);
    }

    public ResponseEntity<String> deleteById(Long id, String tenantId) {
        Patient patient = patientRepository.findByIdAndTenantId(id,tenantId)
                .orElseThrow(()-> new RuntimeException("Unauthorized: Patient does not belong to your organization."));
        patientRepository.delete(patient);
        return new ResponseEntity<>("Deleted Successfully", HttpStatus.OK);
    }

    public ResponseEntity<PatientDto> verifyForClaim(String aadhaarId,String insuranceId,String tenantId,String role,Long patientId) {
        if(!"ROLE_HOSPITAL".equals(role)){
            throw new RuntimeException("Unauthorized: Only Hospital staff can verify patient details");
        }
        Patient patient = patientRepository.findByIdAndTenantIdAndAadhaarId(patientId, tenantId, aadhaarId)
                .orElseThrow(() -> new RuntimeException("Patient Verification Failed: Record not found or Identity mismatch."));
        if (!patient.getInsuranceId().equals(insuranceId)) {
            throw new RuntimeException("Insurance ID mismatch: The provided ID does not match our registered records.");
        }
        if (!patient.isInsuranceVerified()) {
            patient.setInsuranceVerified(true);
            patientRepository.save(patient);
        }
        PatientDto patientDto = PatientDtoMapper.mapPatientEntityToPatientDto(patient);
        return new ResponseEntity<>(patientDto, HttpStatus.OK);
    }
}
