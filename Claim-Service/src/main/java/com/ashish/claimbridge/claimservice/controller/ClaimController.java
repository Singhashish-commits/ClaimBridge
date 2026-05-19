package com.ashish.claimbridge.claimservice.controller;

import com.ashish.claimbridge.claimservice.dto.ApiResponse;
import com.ashish.claimbridge.claimservice.dto.ClaimSubmitDto;
import com.ashish.claimbridge.claimservice.service.ClaimService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

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



}
