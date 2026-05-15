package com.ashish.claimbridge.patientservice.repository;

import com.ashish.claimbridge.patientservice.model.Insurer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface InsurerRepository extends JpaRepository<Insurer, Integer> {


    Optional<Insurer> findByTenantId(String tenantId);
}
