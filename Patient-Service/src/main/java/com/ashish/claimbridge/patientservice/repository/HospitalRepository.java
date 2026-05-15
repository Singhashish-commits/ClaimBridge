package com.ashish.claimbridge.patientservice.repository;

import com.ashish.claimbridge.patientservice.model.Hospital;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface HospitalRepository extends JpaRepository<Hospital, Integer> {

    Optional<Hospital> findByTenantId(String tenantId);
}
