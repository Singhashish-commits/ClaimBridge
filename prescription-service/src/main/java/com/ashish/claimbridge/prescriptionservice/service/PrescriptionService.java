package com.ashish.claimbridge.prescriptionservice.service;

import com.ashish.claimbridge.prescriptionservice.dto.ApiResponse;
import com.ashish.claimbridge.prescriptionservice.dto.DrugDto;
import com.ashish.claimbridge.prescriptionservice.dto.PrescriptionDto;
import com.ashish.claimbridge.prescriptionservice.feignClient.PatientClient;
import com.ashish.claimbridge.prescriptionservice.mapper.PrescriptionDtoMapper;
import com.ashish.claimbridge.prescriptionservice.mapper.PrescriptionMapper;
import com.ashish.claimbridge.prescriptionservice.model.Prescription;
import com.ashish.claimbridge.prescriptionservice.model.PrescriptionStatus;
import com.ashish.claimbridge.prescriptionservice.repository.DrugRepository;
import com.ashish.claimbridge.prescriptionservice.repository.PrescriptionRepository;
import jakarta.persistence.EntityNotFoundException;
import org.jspecify.annotations.Nullable;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class PrescriptionService {
    private final PatientClient patientClient;
    private final PrescriptionRepository prescriptionRepository;
    private final PrescriptionMapper prescriptionMapper;
    private final PrescriptionDtoMapper prescriptionDtoMapper;
    private final DrugRepository drugRepository;
    @Autowired
    public PrescriptionService(PatientClient patientClient, PrescriptionRepository prescriptionRepository, PrescriptionMapper prescriptionMapper, PrescriptionDtoMapper prescriptionDtoMapper,DrugRepository drugRepository) {
        this.patientClient = patientClient;
        this.prescriptionRepository = prescriptionRepository;
        this.prescriptionMapper = prescriptionMapper;
        this.prescriptionDtoMapper = prescriptionDtoMapper;
        this.drugRepository=drugRepository;
    }
    public ApiResponse createPrescription(PrescriptionDto dto,
                                                          String tenantId,
                                                          String role, String email) {
        if(!role.equals("ROLE_HOSPITAL") && !role.equals("ROLE_HOSPITAL_USER")){
            throw new RuntimeException("Unauthorized  to add Prescription");
        }
        try {
            patientClient.getPatient(dto.getPatientId(), tenantId, role);
        } catch (Exception e) {
            throw new RuntimeException("Patient not found! for Prescription" + e.getMessage());
        }
         Prescription prescription= prescriptionMapper.fromDtoToPrescription(dto,tenantId);
        prescriptionRepository.save(prescription);
        return new ApiResponse("prescription added Successfully",true);
    }

    public List<PrescriptionDto> getPrescriptionsByPatientId(Long patientId, String tenantId, String role) {
        if(!role.equals("ROLE_HOSPITAL") && !role.equals("ROLE_HOSPITAL_USER")){
            throw new RuntimeException("Unauthorized  to retrieve Prescription of patient ");
        }
        List<Prescription> list = prescriptionRepository.findByPatientIdAndTenantId(patientId,tenantId)
                .orElseThrow(()-> new RuntimeException("Patient's Prescription not found!"));
       List<PrescriptionDto> dto= list.stream().map(prescriptionDtoMapper::mapToDto).toList();

        return dto;

    }

    public PrescriptionDto getPrescriptionById(Long id, String tenantId, String role) {
        if(!role.equals("ROLE_HOSPITAL") && !role.equals("ROLE_HOSPITAL_USER")){
            throw new RuntimeException("Unauthorized  to retrieve Prescription of patient ");
        }
        Prescription prescription= prescriptionRepository.findByIdAndTenantId(id, tenantId)
                .orElseThrow(()-> new RuntimeException("Prescription not found!"));
        PrescriptionDto dto = prescriptionDtoMapper.mapToDto(prescription);
        return dto;
    }

    public ApiResponse dispense(Long id, String tenantId, String role) {
        if(!role.equals("ROLE_HOSPITAL") && !role.equals("ROLE_HOSPITAL_USER")){
            throw new RuntimeException("Unauthorized  to retrieve Prescription of patient ");
        }
        Prescription prescription = prescriptionRepository.findByIdAndTenantId(id,tenantId)
                .orElseThrow(()-> new RuntimeException("Prescription not found!"));

        if(prescription.getPrescriptionStatus().equals(PrescriptionStatus.DISPENSED))
            throw new RuntimeException("Already Dispensed");

        prescription.setPrescriptionStatus(PrescriptionStatus.DISPENSED);
        prescriptionRepository.save(prescription);
        return new ApiResponse("prescription dispensed Successfully !!",true);
    }

    public PrescriptionDto validateForClaim(Long id, String tenantId, String role) {
        System.out.println("role received from claimService is "+role+tenantId);
        if(!"ROLE_INSURER".equals(role) && !"ROLE_INSURER_USER".equals(role) && !"SYSTEM_INTERNAL".equals(role)){
            throw new RuntimeException("Unauthorized to validate for claim ");
        }
        Prescription prescription= prescriptionRepository.findByIdAndTenantId(id,tenantId)
                .orElseThrow(()-> new EntityNotFoundException("Prescription not found!"));

        if(!prescription.getPrescriptionStatus().equals(PrescriptionStatus.ACTIVE)
                && !prescription.getPrescriptionStatus().equals(PrescriptionStatus.DISPENSED)){
            throw new RuntimeException("Prescription must be Active or Dispensed to process a claim.");
        }
         boolean expired =prescription
                 .getItemList()
                 .stream().anyMatch(item->item.getExpiryDate().isBefore(LocalDateTime.now()));
        if (expired) {
            prescription.setPrescriptionStatus(PrescriptionStatus.EXPIRED);
            prescriptionRepository.save(prescription);
            throw new RuntimeException("One or more Prescription Item has expired");
        }
        if(prescription.getPrescriptionStatus().equals(PrescriptionStatus.EXPIRED)){
            throw new RuntimeException("Prescription has expired");
        }
        PrescriptionDto prescriptionDto = prescriptionDtoMapper.mapToDto(prescription);
            return  prescriptionDto;
    }



}
