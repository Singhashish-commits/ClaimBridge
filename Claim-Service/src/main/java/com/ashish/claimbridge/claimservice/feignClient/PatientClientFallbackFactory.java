package com.ashish.claimbridge.claimservice.feignClient;


import com.ashish.claimbridge.claimservice.dto.InsurancePolicyDto;
import com.ashish.claimbridge.claimservice.dto.PatientDto;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cloud.openfeign.FallbackFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

@Component
public class PatientClientFallbackFactory implements FallbackFactory<PatientClient> {
    private static final Logger log = LoggerFactory.getLogger(PatientClientFallbackFactory.class);


    @Override
    public PatientClient create(Throwable cause) {
        log.error("### FALLBACK FACTORY INVOKED ### cause: {}", cause.getMessage());
        return new PatientClient() {
            @Override
            public ResponseEntity<PatientDto> getPatientById(Long id, String tenantId, String role) {
                log.warn("Patient service fallback triggered for id: {}", id, cause.getMessage(), cause);
                PatientDto patient = new PatientDto();
                patient.setId(id);
                patient.setFirstName("UNKNOWN");
                patient.setLastName("UNKNOWN");
                patient.setInsuranceId("UNKNOWN");
                patient.setHospitalId("UNKNOWN");
                return ResponseEntity.ok(patient);
            }

            @Override
            public ResponseEntity<InsurancePolicyDto> verifyPolicyForClaim(Long patientId, String tenantId, String insurerId) {
                log.warn("Insurance policy verification fallback triggered for patientId: {}", patientId, cause.getMessage(), cause);
                return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE).build();
            }

            @Override
            public ResponseEntity<InsurancePolicyDto> findByIdAndPatientId(Long policyId, Long patientId, String role) {
                log.warn("Policy lookup fallback triggered for policyId: {}, patientId: {}", policyId, patientId, cause.getMessage(), cause);
                return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE).build();
            }

        };


    }
}
