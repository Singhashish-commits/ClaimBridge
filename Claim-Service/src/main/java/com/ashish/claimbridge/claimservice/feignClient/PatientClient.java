package com.ashish.claimbridge.claimservice.feignClient;

import com.ashish.claimbridge.claimservice.dto.PatientDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestHeader;

@FeignClient(name = "patient-service")
public interface PatientClient {
    @GetMapping("/api/patients/get-patient/{id}")
    ResponseEntity<PatientDto> getPatientById(@PathVariable("id") Long id,
                                              @RequestHeader("tenantId") String tenantId,
                                              @RequestHeader("role") String role);


}
