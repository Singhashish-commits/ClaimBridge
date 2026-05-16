package com.ashish.claimbridge.patientservice.controller;

import com.ashish.claimbridge.patientservice.dto.ApiResponse;
import com.ashish.claimbridge.patientservice.dto.ClaimVerifyDto;
import com.ashish.claimbridge.patientservice.dto.PatientDto;
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
   public ResponseEntity<ApiResponse> addPatient(@RequestBody PatientDto patient,
                                                 @RequestHeader("tenantId")  String tenantId,
                                                 @RequestHeader("role") String role,
                                                 @RequestHeader("email") String email) {
        System.out.println("Request Received here ");
            return patientService.savePatient(patient,role,tenantId);
    }

    @GetMapping("/my-patients")
    public ResponseEntity<List<PatientDto>> getAllPatients(@RequestHeader("tenantId")String tenantId,@RequestHeader("role")String role) {
         return   patientService.getPatientByTenantId(tenantId,role);

    }
    @GetMapping("/get-patient/{id}")
    public ResponseEntity<PatientDto> getPatientById(@PathVariable("id") Long id,
                                                     @RequestHeader("tenantId") String tenantId,
                                                     @RequestHeader("role")String role) {
       return  patientService.findById(id,tenantId,role);

    }
    @PutMapping("/update-patent/{id}")
    public ResponseEntity<ApiResponse> updatePatient(
            @PathVariable("id")Long id, @RequestBody PatientDto patient,
            @RequestHeader("tenantId")String tenantId,
            @RequestHeader("role") String role) {
         return patientService.updatePatient(id,patient,tenantId,role);

    }

    @PostMapping("/delete-patient/{id}")
    public ResponseEntity<String> deletePatient(@PathVariable("id")Long id,
                                                @RequestHeader("tenantId") String tenantId,
                                                @RequestHeader("role")String role) {
       return  patientService.deleteById(id,tenantId,role);

    }

    @PostMapping("/verify-claim/{id}")
        public ResponseEntity<ApiResponse> verifyForClaim(
                @PathVariable Long id,
                @RequestBody ClaimVerifyDto claimVerifyDto,
                @RequestHeader("role")String role,
                @RequestHeader("tenantId")String tenantId){
       return  patientService.verifyForClaim(id,claimVerifyDto,role,tenantId);

    }






}
