package com.ashish.claimbridge.fraudservice.repository;

import com.ashish.claimbridge.fraudservice.model.FraudEvaluationRecord;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;

@Repository
public interface FraudRepository  extends JpaRepository<FraudEvaluationRecord,Long> {

    boolean existsByPatientIdAndPrescriptionIdAndEvaluationDateAfter(Long patientId, Long prescriptionId, LocalDateTime date);

    int countByHospitalIdAndPatientIdAndEvaluationDateAfter(String hospitalId, Long patientId, LocalDateTime twoHourAgo);
}
