package com.ashish.claimbridge.patientservice.service;

import com.ashish.claimbridge.patientservice.dto.ApiResponse;
import com.ashish.claimbridge.patientservice.dto.InsurancePolicyDto;
import com.ashish.claimbridge.patientservice.mapper.InsurancePolicyDtoMapper;
import com.ashish.claimbridge.patientservice.model.InsurencePolicy;
import com.ashish.claimbridge.patientservice.model.PolicyStatus;
import com.ashish.claimbridge.patientservice.repository.InsurancePolicyRepository;
import com.ashish.claimbridge.patientservice.repository.InsurerRepository;
import com.ashish.claimbridge.patientservice.repository.PatientRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.time.LocalDate;

@Service
public class InsurancePolicyService {
    private final InsurerRepository insurerRepository;
    private final PatientRepository patientRepository;
    public final InsurancePolicyRepository insurancePolicyRepository;
    @Autowired
    public InsurancePolicyService(InsurerRepository insurerRepository,
                                  PatientRepository patientRepository,
                                  InsurancePolicyRepository insurancePolicyRepository) {
        this.insurerRepository = insurerRepository;
        this.patientRepository = patientRepository;
        this.insurancePolicyRepository = insurancePolicyRepository;
    }


   public ResponseEntity<ApiResponse> enrollPolicy(InsurancePolicyDto dto, String role, String tenantId) {
       if (!"ROLE_HOSPITAL".equals(role) && !"ROLE_HOSPITAL_USER".equals(role)) {
           throw new RuntimeException("Unauthorized to Enroll Policy for  Patient!");
       }
       patientRepository.findByIdAndTenantId(dto.getPatientId(), tenantId)
               .orElseThrow(() -> new RuntimeException("Patient not found! for policy Enrollment!"));


       InsurencePolicy policy = new InsurencePolicy();
       policy.setPatientId(dto.getPatientId());
       policy.setInsurerId(dto.getInsurerId());
       policy.setCoverageLimit(dto.getCoverageLimit());
       policy.setUsedAmount(0.0);
       policy.setValidFrom(dto.getValidFrom());
       policy.setValidTo(dto.getValidTo());
       policy.setStatus(PolicyStatus.ACTIVE);
       policy.setTenantId(tenantId);
       policy.setPolicyNumber(dto.getPolicyNumber());
       insurancePolicyRepository.save(policy);
       return new ResponseEntity<>
               ( new ApiResponse("Policy Enrolled Successfully",true), HttpStatus.ACCEPTED);
   }

    public ResponseEntity<InsurancePolicyDto> getPolicyByNumber(String policyNumber, String tenantId, String role) {
        if(!role.equals("ROLE_HOSPITAL") && !role.equals("ROLE_HOSPITAL_USER")
                && !"ROLE_INSURER".equals(role) && !"ROLE_INSURER_USER".equals(role)) {
            throw new RuntimeException("Unauthorized to Get Policy By Number!");
        }
        InsurencePolicy policy = insurancePolicyRepository.findByPolicyNumberAndTenantId(policyNumber,tenantId)
                .orElseThrow(() -> new RuntimeException("Policy Not Found!"));
       InsurancePolicyDto dto= InsurancePolicyDtoMapper.mapDto(policy);
       return new ResponseEntity<>(dto, HttpStatus.OK);
    }

    public ResponseEntity<ApiResponse> updatePolicyStatus(Long id, String status, String tenantId, String role) {
        if (!"ROLE_HOSPITAL".equals(role) && !"ROLE_INSURER".equals(role)) {
            throw new RuntimeException("Unauthorized!");
        }
        InsurencePolicy policy = insurancePolicyRepository.findByIdAndTenantId(id,tenantId)
                .orElseThrow(() -> new RuntimeException("Policy Not Found!"));
        policy.setStatus(PolicyStatus.valueOf(status));
        insurancePolicyRepository.save(policy);
        return new ResponseEntity<>(
                new ApiResponse("Updated Successfully",true),HttpStatus.OK
        );
    }
    public ResponseEntity<InsurancePolicyDto> verifyPolicyForClaim(Long patientId, String tenantId, String insurerId) {
        InsurencePolicy policy = insurancePolicyRepository.findByPatientIdAndPolicyNumber(patientId,insurerId)
                .orElseThrow(() -> new RuntimeException("Policy Not Found!"));
        if (policy.getStatus() != PolicyStatus.ACTIVE) {
            throw new RuntimeException("Policy is not active!");
        }
        if (policy.getValidTo().isBefore(LocalDate.now())) {
            policy.setStatus(PolicyStatus.EXPIRED);
            insurancePolicyRepository.save(policy);
            throw new RuntimeException("Policy is expired!");

        }

       InsurancePolicyDto dto= InsurancePolicyDtoMapper.mapDto(policy);
        return ResponseEntity.ok(dto);


    }
}
