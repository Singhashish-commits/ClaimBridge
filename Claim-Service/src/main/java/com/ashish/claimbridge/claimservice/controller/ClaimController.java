package com.ashish.claimbridge.claimservice.controller;

import com.ashish.claimbridge.claimservice.dto.*;
import com.ashish.claimbridge.claimservice.model.ClaimHistory;
import com.ashish.claimbridge.claimservice.model.ClaimStatus;
import com.ashish.claimbridge.claimservice.service.ClaimService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/claims")
public class ClaimController {
    private ClaimService claimService;
    @Autowired
    public ClaimController(ClaimService claimService){
        this.claimService= claimService;
    }
    @PostMapping("/submit-claim")
    public ResponseEntity<ApiResponse> submitClaim(
            @RequestBody  ClaimSubmitDto claimSubmitDto,
            @RequestHeader("tenantId")String tenantId,@RequestHeader("role")String role,
            @RequestHeader("email")String email){
     return    claimService.submitClaim(claimSubmitDto,tenantId,role,email);
    }
    @GetMapping("/{id}")
    public ResponseEntity<ClaimResponse> getClaimById(@PathVariable("id")Long id,
                                                      @RequestHeader("tenantId")String tenantId,
                                                      @RequestHeader("role")String role){
       return  claimService.getClaimById(id,tenantId,role);

    }

    @GetMapping("/hospital/{id}")
    public ResponseEntity<List<ClaimResponse>> getClaimByHospitalId(@PathVariable("id")Long id,
                                                                    @RequestHeader("tenantId")String tenantId,
                                                                    @RequestHeader("role")String role){
        return claimService.getClaimByHospitalId(id,tenantId,role);

    }
    @GetMapping("/status/{status}")
    public ResponseEntity<List<ClaimResponse>> getClaimByStatus(@PathVariable ClaimStatus status,
                                                                 @RequestHeader("tenantId")String tenantId,
                                                                 @RequestHeader("role")String role){
        return claimService.getClaimByStatus(status,tenantId,role);
    }
    @PatchMapping("/approve/{id}")
    public ResponseEntity<ApiResponse> approveClaim(@PathVariable("id")Long id,@RequestHeader("tenantId")String tenantId,
                                                    @RequestHeader("role")String role,
                                                    @RequestBody  ClaimApproveDto claimApproveDto,
                                                    @RequestHeader("email") String email){
        return claimService.approveClaim(id, claimApproveDto,tenantId,role,email);
    }

    @PatchMapping("/{id}/reject")
    public ResponseEntity<ApiResponse> rejectClaim(
            @PathVariable Long id,
            @RequestHeader("tenantId") String tenantId,
            @RequestHeader("role") String role,
            @RequestParam String reason) {
        return claimService.rejectClaimById(id, tenantId, role, reason);
    }

    @PatchMapping("/{id}/cancel")
    public ResponseEntity<ApiResponse> cancelClaim(
            @PathVariable Long id,
            @RequestHeader("tenantId") String tenantId,
            @RequestHeader("role") String role,
            @RequestParam String reason) {
        return claimService.cancelClaimById(id, tenantId, role, reason);
    }

    @GetMapping("/{id}/history")
    public ResponseEntity<List<ClaimHistoryDto>> getClaimHistory(
            @PathVariable Long id,
            @RequestHeader("tenantId")String tenantId,
            @RequestHeader("role")String role) {
        return claimService.getClaimHistory(id, tenantId, role);
    }


//    @GetMapping("/stats")
//    public ResponseEntity<ClaimStatsDto> getStats(
//            @RequestHeader("tenantId") String tenantId,
//            @RequestHeader("role") String role) {
//        return claimService.getClaimStats(tenantId, role);
//    }






}
