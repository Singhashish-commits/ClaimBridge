package com.ashish.claimbridge.claimservice.feignClient;

import com.ashish.claimbridge.claimservice.dto.PrescriptionDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestHeader;

@FeignClient( name= "prescription-service",fallbackFactory = PrescriptionClientFallbackFactory.class)

public interface PrescriptionClient {
    @GetMapping("api/prescriptions/validate/{id}")
    ResponseEntity<PrescriptionDto> validateForClaim(@PathVariable("id")Long id,
            @RequestHeader("tenantId") String tenantId,
                                                     @RequestHeader("role")String role);
}
