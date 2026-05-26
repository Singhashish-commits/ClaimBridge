package com.ashish.claimbridge.prescriptionservice.repository;

import com.ashish.claimbridge.prescriptionservice.model.Prescription;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface PrescriptionRepository extends JpaRepository<Prescription, Long> {
    Optional<List<Prescription>> findByPatientIdAndTenantId(Long patientId, String tenantId);

    Optional<Prescription> findByIdAndTenantId(Long id, String tenantId);

    Optional<Prescription> findByPatientIdAndInsurerId(Long patientId, String insurerId);
}
