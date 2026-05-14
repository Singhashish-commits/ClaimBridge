package com.ashish.claimbridge.patientservice.repository;

import com.ashish.claimbridge.patientservice.model.Patient;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PatientRepository extends CrudRepository<Patient, Long> {
    Optional<List<Patient>> findByTenantId(String tenantId);
    Optional<Patient> findById(Long id);
    Optional<Patient> findByIdAndTenantId(Long id, String tenantId);

}
