package com.ashish.claimbridge.claimservice.feignClient;

import com.ashish.claimbridge.claimservice.dto.PrescriptionDto;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cloud.openfeign.FallbackFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.Collections;
@Component
public class PrescriptionClientFallbackFactory  implements FallbackFactory<PrescriptionClient> {
    private static final Logger log = LoggerFactory.getLogger(PrescriptionClientFallbackFactory.class);

    @Override
    public PrescriptionClient create(Throwable cause) {
     return (id, tenantId, role) -> {
         log.warn("Patient service fallback triggered for id: {}", id, cause.getMessage(), cause);
        PrescriptionDto dto = new PrescriptionDto();
         dto.setPatientId(id);
         dto.setDoctorName("UNKNOWN");
         dto.setIssuedDate(LocalDate.now());
         dto.setPrescriptionStatus("SERVICE_UNAVAILABLE");
         dto.setItems(Collections.emptyList());
         return ResponseEntity.ok(dto);
     };
    }
}
