package com.ashish.claimbridge.claimservice.controller;

import com.ashish.claimbridge.claimservice.dto.ApiResponse;
import com.ashish.claimbridge.claimservice.dto.ClaimApproveDto;
import com.ashish.claimbridge.claimservice.dto.ClaimResponse;
import com.ashish.claimbridge.claimservice.dto.ClaimSubmitDto;
import com.ashish.claimbridge.claimservice.model.ClaimStatus;
import com.ashish.claimbridge.claimservice.service.ClaimService;
import jakarta.ws.rs.Path;
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
                                                                 @RequestHeader("tenatId")String tenantId,
                                                                 @RequestHeader("role")String role){
        return claimService.getClaimByStatus(status,tenantId,role);
    }
    @PatchMapping("/approve/{id}")
    public ResponseEntity<ApiResponse> approveClaim(@PathVariable("id")Long id,@RequestHeader("tenantId")String tenantId,
                                                    @RequestHeader("role")String role,
                                                    @RequestBody  ClaimApproveDto claimApproveDto){
        return claimService.approveClaim(id, claimApproveDto,tenantId,role);
    }






}
