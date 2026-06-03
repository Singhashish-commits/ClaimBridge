package com.ashish.claimbridge.patientservice.repository;

import com.ashish.claimbridge.patientservice.model.InsurancePolicy;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface InsurancePolicyRepository extends JpaRepository<InsurancePolicy,Long> {
    Optional<InsurancePolicy> findByPolicyNumber(String policyNumber);
    Optional<InsurancePolicy> findByPatientIdAndPolicyNumber(Long patientId, String policyNumber);
    Optional<InsurancePolicy> findByIdAndTenantId(Long id, String tenantId);

    Optional<InsurancePolicy> findByIdAndPatientId(Long id, Long patientId);

    boolean existsByPatientIdAndPolicyNumber(Long patientId,String policyNumber);

    List<InsurancePolicy> findByInsurerId(String insurerId);
}
