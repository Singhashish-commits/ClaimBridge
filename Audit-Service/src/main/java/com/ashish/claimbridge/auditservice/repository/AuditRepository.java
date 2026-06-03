package com.ashish.claimbridge.auditservice.repository;

import com.ashish.claimbridge.auditservice.event.AuditEvent;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface AuditRepository extends JpaRepository<AuditEvent, Long> {

    List<AuditEvent> findByClaimIdAndHospitalId(Long claimId, String tenantId);

    List<AuditEvent> findByClaimIdAndInsurerId(Long claimId, String insurerId);

    List<AuditEvent> findByPatientIdAndHospitalId(Long patientId, String hospitalId);

    List<AuditEvent> findByPatientIdAndInsurerId(Long patientId, String insurerId);

    List<AuditEvent> findByHospitalId(String hospitalId);

    List<AuditEvent> findByInsurerId(String insurerId);

    List<AuditEvent> findByEventTypeAndHospitalId(String eventType, String hospitalId);

    List<AuditEvent> findByEventTypeAndInsurerId(String eventType, String insurerId);
}
