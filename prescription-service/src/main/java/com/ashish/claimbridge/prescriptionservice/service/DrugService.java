package com.ashish.claimbridge.prescriptionservice.service;

import com.ashish.claimbridge.prescriptionservice.dto.ApiResponse;
import com.ashish.claimbridge.prescriptionservice.dto.DrugDto;
import com.ashish.claimbridge.prescriptionservice.dto.DrugInsurerDto;
import com.ashish.claimbridge.prescriptionservice.dto.PatientDto;
import com.ashish.claimbridge.prescriptionservice.feignClient.PatientClient;
import com.ashish.claimbridge.prescriptionservice.model.Drug;
import com.ashish.claimbridge.prescriptionservice.repository.DrugRepository;
import jakarta.persistence.EntityNotFoundException;
import org.jspecify.annotations.Nullable;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class DrugService {
    private final DrugRepository drugRepository;
    private final PatientClient patientClient;
            @Autowired
            public DrugService(DrugRepository drugRepository, PatientClient patientClient) {
                this.drugRepository = drugRepository;
                this.patientClient = patientClient;
            }

    public List<DrugDto> searchDrug(String name, Long patientId, String role, String tenantId) {
        ResponseEntity<PatientDto> check = patientClient.getPatient(patientId,tenantId,role);
        PatientDto patientDto = check.getBody();
        String insurerId = patientDto.getInsuranceId();
        return drugRepository.findByDrugNameContainingIgnoreCaseAndInsurerId(name,insurerId).stream().map(drug ->new DrugDto(
                drug.getDrugCode(),
                drug.getDrugName(),
                drug.getCategory()
        )).toList();

    }

    public  DrugDto findByDrugCode(String drugCode,Long patientId, String role, String tenantId ) {
                ResponseEntity<PatientDto> check = patientClient.getPatient(patientId,tenantId,role);
                PatientDto patientDto = check.getBody();
                String insurerId = patientDto.getInsuranceId();
                Drug drug = drugRepository.findByDrugCodeAndInsurerId(drugCode,insurerId)
                        .orElseThrow(()-> new EntityNotFoundException("Drug Doesnt Exist"));
                return new DrugDto(drug.getDrugCode(),drug.getCategory(),drug.getDrugName());
    }

    public  DrugInsurerDto addDrug(DrugInsurerDto dto, String role, String tenantId) {
                if(!"ROLE_INSURER".equals(role) && !"ROLE_INSURER_USER".equals(role)) {
                    throw new IllegalStateException("Invalid Role");
                }
                if(drugRepository.findByDrugCodeAndInsurerId(dto.getDrugCode(),tenantId).isPresent()){
                    throw new RuntimeException("Drug Already Exist for this Insurer ID");
                }
                Drug drug = new Drug();
        return getDrugInsurerDto(dto, tenantId, drug);
    }

    public DrugInsurerDto updateDrugById(Long id, DrugInsurerDto dto, String role, String tenantId) {
                if(!"ROLE_INSURER".equals(role) && !"ROLE_INSURER_USER".equals(role)) {
                    throw new IllegalStateException("Invalid Role");
                }
               Drug drug= drugRepository.findById(id).orElseThrow(()-> new EntityNotFoundException("Drug with this id doesnt exist"));
                if(!drug.getInsurerId().equals(tenantId)){
                    throw new IllegalStateException("Not Authorized to Update Details");
                }
        return getDrugInsurerDto(dto, tenantId, drug);


    }



    public  ApiResponse deleteById(Long id, String role, String tenantId) {
                if(!"ROLE_INSURER".equals(role)&& !"ROLE_INSURER_USER".equals(tenantId)) {
                    throw new IllegalStateException("Invalid Role to Modify Data");
                }
                Drug drug = drugRepository.findById(id)
                        .orElseThrow(()-> new EntityNotFoundException("Drug with this id doesnt exist"));
                if(!drug.getInsurerId().equals(tenantId)){
                    throw new IllegalStateException("Not Authorized to Delete Details");
                }
                drugRepository.deleteById(id);
                return new ApiResponse(" Drug with id"+ id +" Deleted Successfully",true);

    }

    private DrugInsurerDto getDrugInsurerDto(DrugInsurerDto dto, String tenantId, Drug drug) {
        drug.setDrugName(dto.getDrugName());
        drug.setCategory(dto.getCategory());
        drug.setDrugCode(dto.getDrugCode());
        drug.setStandardCost(dto.getStanderCost());
        drug.setInsurerId(tenantId);
        drugRepository.save(drug);
        return dto;
    }
}
