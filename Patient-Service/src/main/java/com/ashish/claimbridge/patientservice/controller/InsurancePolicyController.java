package com.ashish.claimbridge.patientservice.controller;

import com.ashish.claimbridge.patientservice.dto.ApiResponse;
import com.ashish.claimbridge.patientservice.dto.InsurancePolicyDto;
import com.ashish.claimbridge.patientservice.service.InsurancePolicyService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/api/insurence")
public class InsurancePolicyController {
    private final InsurancePolicyService insurancePolicyService;
    @Autowired
    public InsurancePolicyController(InsurancePolicyService insurancePolicyService) {
        this.insurancePolicyService = insurancePolicyService;
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
        return insurancePolicyService.getPolicyByNumber(policyNumber,tenantId,role);


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

}
