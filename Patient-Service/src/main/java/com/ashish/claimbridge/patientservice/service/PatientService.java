package com.ashish.claimbridge.patientservice.service;

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


    public ResponseEntity<Patient>  savePatient(Patient patient,String role,String tenantId) {
        if(!"ROLE_HOSPITAL".equals(role)){
            throw new RuntimeException("Invalid role To Add Patient");
        }
        patient.setTenantId(tenantId);
        return ResponseEntity.ok(patientRepository.save(patient));

    }

    public ResponseEntity<List<Patient>> getPatientByTenantId(String tenantId) {

        return new ResponseEntity<>(patientRepository.findByTenantId(tenantId).orElseThrow(()-> new RuntimeException("not Patient available for tenantId"+tenantId)), HttpStatus.OK);


    }

    public ResponseEntity<Patient> findById(Long id,String tenantId) {
       Patient p = patientRepository.findByIdAndTenantId(id,tenantId).orElseThrow(()-> new RuntimeException("not Available"));
       return ResponseEntity.ok(p);

    }



    public ResponseEntity<Patient> updatePatient(long id, Patient patient, String tenantId) {
        Patient current = patientRepository.findByIdAndTenantId(id, tenantId)
                .orElseThrow(()-> new RuntimeException("Patient not found with id " + id+"and Hospital id " + tenantId) );
        current.setFirstName(patient.getFirstName());
        current.setLastName(patient.getLastName());
        current.setEmail(patient.getEmail());
        current.setGender(patient.getGender());
        current.setMobileNumber(patient.getMobileNumber());


        return new ResponseEntity<>(patientRepository.save(current), HttpStatus.OK);



    }

    public ResponseEntity<String> deleteById(Long id, String tenantId) {
        Patient patient = patientRepository.findByIdAndTenantId(id,tenantId).orElseThrow(()-> new RuntimeException("Unauthorized: Patient does not belong to your organization"));
        patientRepository.delete(patient);
        return new ResponseEntity<>("Deleted Sucessfully", HttpStatus.OK);
    }
}
