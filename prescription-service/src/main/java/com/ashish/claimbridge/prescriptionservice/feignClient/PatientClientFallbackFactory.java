package com.ashish.claimbridge.prescriptionservice.feignClient;

import com.ashish.claimbridge.prescriptionservice.dto.PatientDto;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cloud.openfeign.FallbackFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

@Component
public class PatientClientFallbackFactory implements FallbackFactory<PatientClient> {
    private final Logger log = LoggerFactory.getLogger(this.getClass());
    @Override
    public PatientClient create(Throwable cause) {
        log.warn("Patient service fallback triggered for id: {}", cause.getMessage(), cause);
        return new PatientClient() {

            @Override
            public ResponseEntity<PatientDto> getPatient(Long id, String tenantId, String role) {
               PatientDto patientDto = new PatientDto();
               patientDto.setId(id);
              patientDto.setFirstName("Unknown");
              patientDto.setLastName("Unknown");
              patientDto.setInsuranceId("Unknown");
              patientDto.setHospitalId(tenantId);
               return new ResponseEntity<>(patientDto, HttpStatus.SERVICE_UNAVAILABLE);
            }
        };
    }
}
