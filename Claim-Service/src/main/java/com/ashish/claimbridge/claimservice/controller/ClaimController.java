package com.ashish.claimbridge.claimservice.controller;

import com.ashish.claimbridge.claimservice.dto.*;
import com.ashish.claimbridge.claimservice.model.ClaimStatus;
import com.ashish.claimbridge.claimservice.service.ClaimService;
import com.fasterxml.jackson.core.JsonProcessingException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/claims")
public class ClaimController {
    private  final ClaimService claimService;
    @Autowired
    public ClaimController(ClaimService claimService){
        this.claimService= claimService;
    }
    @PostMapping("/submit-claim")
    public ResponseEntity<ApiResponse> submitClaim(
            @RequestBody  ClaimSubmitDto claimSubmitDto,
            @RequestHeader("tenantId")String tenantId,@RequestHeader("role")String role,
            @RequestHeader("email")String email){
     ApiResponse response = claimService.submitClaim(claimSubmitDto,tenantId,role,email);
     return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }
    @GetMapping("/{id}")
    public ResponseEntity<ClaimResponse> getClaimById(@PathVariable("id")Long id,
                                                      @RequestHeader("tenantId")String tenantId,
                                                      @RequestHeader("role")String role){

       return ResponseEntity.ok(claimService.getClaimById(id,tenantId,role));

    }

    @GetMapping("/hospital")
    public ResponseEntity<List<ClaimResponse>> getClaimByHospitalId(@RequestHeader("tenantId")String tenantId,
                                                                    @RequestHeader("role")String role){
        return ResponseEntity.ok(claimService.getClaimByHospitalId(tenantId,role));

    }
    @GetMapping("/status/{status}")
    public ResponseEntity<List<ClaimResponse>> getClaimByStatus(@PathVariable ClaimStatus status,
                                                                 @RequestHeader("tenantId")String tenantId,
                                                                 @RequestHeader("role")String role){
        return ResponseEntity.ok(claimService.getClaimByStatus(status,tenantId,role));
    }
    @PatchMapping("/approve/{id}")
    public ResponseEntity<ApiResponse> approveClaim(@PathVariable("id")Long id,@RequestHeader("tenantId")String tenantId,
                                                    @RequestHeader("role")String role,
                                                    @RequestBody  ClaimApproveDto claimApproveDto,
                                                    @RequestHeader("email") String email) throws JsonProcessingException {
        return ResponseEntity.ok(claimService.approveClaim(id, claimApproveDto,tenantId,role,email));
    }

    @PatchMapping("/{id}/reject")
    public ResponseEntity<ApiResponse> rejectClaim(
            @PathVariable Long id,
            @RequestHeader("tenantId") String tenantId,
            @RequestHeader("role") String role,
            @RequestParam String reason,
            @RequestHeader("email")String email) {
        return ResponseEntity.ok(claimService.rejectClaimById(id, tenantId, role, reason,email));
    }

    @PatchMapping("/{id}/cancel")
    public ResponseEntity<ApiResponse> cancelClaim(
            @PathVariable Long id,
            @RequestHeader("tenantId") String tenantId,
            @RequestHeader("role") String role,
            @RequestParam String reason,
            @RequestHeader("email")String email) {
        return ResponseEntity.ok(claimService.cancelClaimById(id, tenantId, role, reason,email));
    }

    @GetMapping("/{id}/history")
    public ResponseEntity<List<ClaimHistoryDto>> getClaimHistory(
            @PathVariable Long id,
            @RequestHeader("tenantId")String tenantId,
            @RequestHeader("role")String role) {
        return ResponseEntity.ok(claimService.getClaimHistory(id,tenantId,role));
    }


    @GetMapping("/stats")
    public ResponseEntity<ClaimStatsDto> getStats(
            @RequestHeader("tenantId") String tenantId,
            @RequestHeader("role") String role) {
         ClaimStatsDto dto = claimService.getClaimStats(tenantId, role);
        return ResponseEntity.ok(dto);
    }






}
