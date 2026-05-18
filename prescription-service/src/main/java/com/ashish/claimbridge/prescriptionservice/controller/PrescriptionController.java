package com.ashish.claimbridge.prescriptionservice.controller;

import com.ashish.claimbridge.prescriptionservice.dto.ApiResponse;
import com.ashish.claimbridge.prescriptionservice.dto.PrescriptionDto;
import com.ashish.claimbridge.prescriptionservice.service.PrescriptionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("api/prescriptions")
public class PrescriptionController {
    private final PrescriptionService prescriptionService;
    @Autowired
    public PrescriptionController(PrescriptionService prescriptionService) {
        this.prescriptionService = prescriptionService;
    }

    @PostMapping("/issue-new")
    public ResponseEntity<ApiResponse> issuePrescription(@RequestBody PrescriptionDto dto,
                                                         @RequestHeader("tenantId") String tenantId,
                                                         @RequestHeader("role")String role,
                                                         @RequestHeader("email")String email) {
        return prescriptionService.createPrescription(dto,tenantId,role,email);
    }
        @GetMapping("{id}")
    public ResponseEntity<List<PrescriptionDto>> getPrescriptionsByPatientId(@PathVariable Long id,
                                                                     @RequestHeader("tenantId")String tenantId,
                                                                     @RequestHeader("role")String role){
            return prescriptionService.getPrescriptionsByPatientId(id,tenantId,role);
        }
        @GetMapping("prescription/{id}")
public ResponseEntity<PrescriptionDto> getPrescriptionById(@PathVariable Long id,
                                                           @RequestHeader("tenantId")String tenantId,
                                                          @RequestHeader("role")String role){
        return prescriptionService.getPrescriptionById(id,tenantId,role);


}

@PatchMapping("{id}/dispense")
    public ResponseEntity<ApiResponse> dispense(@PathVariable Long id,
                                                @RequestHeader("tenantId")String tenantId,
                                                @RequestHeader("role")String role){
        return prescriptionService.dispense(id,tenantId,role);
}

public ResponseEntity<PrescriptionDto> validateForClaim(@PathVariable Long id,
                                                    @RequestHeader("tenantId")String tenantId,
                                                    @RequestHeader("role")String role){
        return prescriptionService.validateForClaim(id,tenantId,role);
}




}
