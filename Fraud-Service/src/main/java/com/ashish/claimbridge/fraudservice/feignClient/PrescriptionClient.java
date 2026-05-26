package com.ashish.claimbridge.fraudservice.feignClient;

import com.ashish.claimbridge.fraudservice.dto.PrescriptionItemDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestHeader;

import java.util.List;

@FeignClient(name = "prescription-service")
public interface PrescriptionClient {
    @GetMapping("api/prescription/list/{patientId}")
    ResponseEntity<List<PrescriptionItemDto>> ItemListByPateintId(
            @PathVariable("patientId")Long patientId, @RequestHeader("tenantId")String tenantId,
            @RequestHeader("role")String role);

}
