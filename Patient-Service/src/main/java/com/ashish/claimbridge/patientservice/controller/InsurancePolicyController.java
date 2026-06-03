package com.ashish.claimbridge.patientservice.controller;

import com.ashish.claimbridge.patientservice.dto.ApiResponse;
import com.ashish.claimbridge.patientservice.dto.InsurancePolicyDto;
import com.ashish.claimbridge.patientservice.dto.InsurerDto;
import com.ashish.claimbridge.patientservice.service.InsurancePolicyService;
import com.ashish.claimbridge.patientservice.service.PatientService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/insurance")
public class InsurancePolicyController {
    private final InsurancePolicyService insurancePolicyService;
    private final PatientService patientService;

    @Autowired
    public InsurancePolicyController(InsurancePolicyService insurancePolicyService, PatientService patientService) {
        this.insurancePolicyService = insurancePolicyService;
        this.patientService = patientService;
    }

    @PostMapping("/enroll-policy")
    public ResponseEntity<ApiResponse> enrollPolicy(@RequestBody InsurancePolicyDto dto,
                                                    @RequestHeader("role")String role,
                                                    @RequestHeader("tenantId")String tenantId) {
       return  insurancePolicyService.enrollPolicy(dto,role,tenantId);

    }
    @PostMapping("/get-policy/{policyNumber}")
    public ResponseEntity<InsurancePolicyDto>getPolicyByNumber(@PathVariable String policyNumber,
                                                               @RequestHeader("tenantId")String tenantId,
                                                               @RequestHeader("role")String role) {
        return insurancePolicyService.getPolicyByNumber(policyNumber, tenantId, role);


    }

    @PutMapping("update/status/{id}")
    public ResponseEntity<ApiResponse> updateStatus(@PathVariable Long id,
                                                    @RequestParam String status,
                                                    @RequestHeader("tenantId")String tenantId,
                                                    @RequestHeader("role")String role) {
        return insurancePolicyService.updatePolicyStatus(id, status, tenantId, role);


    }

    @GetMapping("/verify/policy")
    public ResponseEntity<InsurancePolicyDto> verifyForClaim(
            @RequestParam Long patientId,
            @RequestParam String insuranceId,
            @RequestHeader("tenantId") String tenantId) {
        return insurancePolicyService.verifyPolicyForClaim(patientId, insuranceId, tenantId);
    }

    @GetMapping("/policy/{policyId}/{patientId}")
    public ResponseEntity<InsurancePolicyDto> findByIdAndPatientId(
            @PathVariable Long policyId,
            @PathVariable Long patientId,
            @RequestHeader("role")String role){
       InsurancePolicyDto dto = insurancePolicyService.findByIdAndPatientId(policyId,patientId,role);
       return ResponseEntity.ok(dto);

    }

    @GetMapping("/policies/insurer")
    public ResponseEntity<List<InsurancePolicyDto>> getAllPolicies(
            @RequestHeader("tenantId") String tenantId,@RequestHeader("role")String role) {
        return ResponseEntity.ok(insurancePolicyService.findByInsurerId(tenantId,role));
    }

    @GetMapping("insurers/search")
    public ResponseEntity<List<InsurerDto>>searchInsurer(@RequestParam String name,
                                                         @RequestHeader("role") String role){
        return ResponseEntity.ok(patientService.searchInsurer(name,role));

    }





}
