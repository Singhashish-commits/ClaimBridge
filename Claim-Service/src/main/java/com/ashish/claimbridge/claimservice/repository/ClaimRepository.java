package com.ashish.claimbridge.claimservice.repository;

import com.ashish.claimbridge.claimservice.model.Claim;
import com.ashish.claimbridge.claimservice.model.ClaimHistory;
import com.ashish.claimbridge.claimservice.model.ClaimStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ClaimRepository extends JpaRepository<Claim, Long> {

    Optional<List<Claim>> findByHospitalId(String hospitalId);
    Optional<Claim> findByIdAndHospitalId(Long id, String hospitalId);
    List<Claim> findByInsurerId(String insurerId);
    List<Claim> findByInsurerIdAndStatus(String insurerId, ClaimStatus status);
    Optional<Claim> findByIdAndInsurerId(Long id, String insurerId);

    boolean existsByPrescriptionIdAndStatusNot(Long prescriptionId, ClaimStatus status);

    Optional<List<Claim>> findByStatusAndHospitalId(ClaimStatus status, String tenantId);

    Optional<List<Claim>> findByStatusAndInsurerId(ClaimStatus status, String insurerId);

}
