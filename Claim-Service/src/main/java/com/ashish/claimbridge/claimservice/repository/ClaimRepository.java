package com.ashish.claimbridge.claimservice.repository;

import com.ashish.claimbridge.claimservice.dto.ClaimStatsSummary;
import com.ashish.claimbridge.claimservice.model.Claim;
import com.ashish.claimbridge.claimservice.model.ClaimHistory;
import com.ashish.claimbridge.claimservice.model.ClaimStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface ClaimRepository extends JpaRepository<Claim, Long> {

    List<Claim> findByHospitalId(String hospitalId);
    Optional<Claim> findByIdAndHospitalId(Long id, String hospitalId);
    List<Claim> findByInsurerId(String insurerId);
    List<Claim> findByInsurerIdAndStatus(String insurerId, ClaimStatus status);
    Optional<Claim> findByIdAndInsurerId(Long id, String insurerId);

    List<Claim> findByStatusAndHospitalId(ClaimStatus status, String tenantId);

   List<Claim> findByStatusAndInsurerId(ClaimStatus status, String insurerId);

    boolean existsByPrescriptionIdAndStatusNotIn(Long prescriptionId, List<ClaimStatus> rejected);

    @Query("SELECT c.status as status, COUNT(c.id) as count, " +
            "SUM(c.totalClaimAmount) as totalClaimed, " +
            "SUM(c.approvedAmount) as totalApproved, " +
            "SUM(c.rejectedAmount) as totalRejected " +
            "FROM Claim c WHERE c.hospitalId = :tenantId GROUP BY c.status")
    List<ClaimStatsSummary> getHospitalStats(@Param("tenantId")String tenantId);

    @Query("SELECT c.status as status, COUNT(c.id) as count, " +
            "SUM(c.totalClaimAmount) as totalClaimed, " +
            "SUM(c.approvedAmount) as totalApproved, " +
            "SUM(c.rejectedAmount) as totalRejected " +
            "FROM Claim c WHERE c.insurerId = :tenantId GROUP BY c.status")
    List<ClaimStatsSummary> getInsurerStats(@Param("tenantId") String tenantId);


}
