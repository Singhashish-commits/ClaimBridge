package com.ashish.claimbridge.patientservice.controller;

import com.ashish.claimbridge.patientservice.model.Patient;
import com.ashish.claimbridge.patientservice.service.PatientService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/patients")
public class PatientController {
    private final PatientService patientService;

    @Autowired
    public PatientController(PatientService patientService) {
        this.patientService = patientService;
    }

    @PostMapping("/add")
   public ResponseEntity<Patient> addPatient(@RequestBody Patient patient,
                                             @RequestHeader("tenantId")  String tenantId,
                                             @RequestHeader("role") String role,
                                             @RequestHeader("email") String email) {
            return patientService.savePatient(patient,role,tenantId);
    }

    @GetMapping("/my-patients")
    public ResponseEntity<List<Patient>> getAllPatients(@RequestHeader("tenantId")String tenantId) {
          return   patientService.getPatientByTenantId(tenantId);

    }
    @GetMapping("/get-patient/{id}")
    public ResponseEntity<Patient> getPatientById(@PathVariable("id") Long id, @RequestHeader("tenantId") String tenantId) {
       return  patientService.findById(id,tenantId);

    }
    @PutMapping("/update-patent/{id}")
    public ResponseEntity<Patient> updatePatient(
            @PathVariable("id")Long id, @RequestBody Patient patient,
            @RequestHeader("tenantId")String tenantId){
         return patientService.updatePatient(id,patient,tenantId);

    }

    @PostMapping("/delete-patient/{id}")
    public ResponseEntity<String> deletePatient(@PathVariable("id")Long id, @RequestHeader("tenantId") String tenantId) {
       return  patientService.deleteById(id,tenantId);

    }






}
