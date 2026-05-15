package com.ashish.claimbridge.patientservice.repository;

import com.ashish.claimbridge.patientservice.model.InsurencePolicy;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface InsurancePolicyRepository extends JpaRepository<InsurencePolicy,Long> {
    Optional<InsurencePolicy> findByPolicyNumberAndTenantId(String policyNumber, String tenantId);
    Optional<InsurencePolicy> findByPatientIdAndPolicyNumber(Long patientId, String policyNumber);
    Optional<InsurencePolicy> findByIdAndTenantId(Long id, String tenantId);
}
