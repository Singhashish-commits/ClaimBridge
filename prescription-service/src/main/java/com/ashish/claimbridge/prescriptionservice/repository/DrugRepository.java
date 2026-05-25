package com.ashish.claimbridge.prescriptionservice.repository;

import com.ashish.claimbridge.prescriptionservice.dto.DrugDto;
import com.ashish.claimbridge.prescriptionservice.model.Drug;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

@Repository
public interface DrugRepository extends JpaRepository<Drug, Long> {

     Optional<Drug> findByDrugCode(String drugCode);


    Optional<Drug> findByDrugCodeAndInsurerId(String drugCode, String insurerId);

    List<DrugDto> findByDrugNameContainingIgnoreCaseAndInsurerId(String drugName, String insurerId);
}
