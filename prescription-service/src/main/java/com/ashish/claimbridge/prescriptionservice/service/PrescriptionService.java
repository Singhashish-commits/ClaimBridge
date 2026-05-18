package com.ashish.claimbridge.prescriptionservice.service;

import com.ashish.claimbridge.prescriptionservice.dto.ApiResponse;
import com.ashish.claimbridge.prescriptionservice.dto.PrescriptionDto;
import com.ashish.claimbridge.prescriptionservice.feignClient.PatientClient;
import com.ashish.claimbridge.prescriptionservice.mapper.PrescriptionDtoMapper;
import com.ashish.claimbridge.prescriptionservice.mapper.PrescriptionMapper;
import com.ashish.claimbridge.prescriptionservice.model.Prescription;
import com.ashish.claimbridge.prescriptionservice.model.PrescriptionStatus;
import com.ashish.claimbridge.prescriptionservice.repository.PrescriptionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class PrescriptionService {
    private final PatientClient patientClient;
    private final PrescriptionRepository prescriptionRepository;
    @Autowired
    public PrescriptionService(PatientClient patientClient, PrescriptionRepository prescriptionRepository) {
        this.patientClient = patientClient;
        this.prescriptionRepository = prescriptionRepository;
    }
    public ResponseEntity<ApiResponse> createPrescription(PrescriptionDto dto,
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
         Prescription prescription= PrescriptionMapper.fromDtoToPrescription(dto,tenantId);
        prescriptionRepository.save(prescription);
        return new ResponseEntity<>(new ApiResponse("prescription added Successfully",true), HttpStatus.OK);
    }

    public ResponseEntity<List<PrescriptionDto>> getPrescriptionsByPatientId(Long patientId, String tenantId, String role) {
        if(!role.equals("ROLE_HOSPITAL") && !role.equals("ROLE_HOSPITAL_USER")){
            throw new RuntimeException("Unauthorized  to retrieve Prescription of patient ");
        }
        List<Prescription> list = prescriptionRepository.findByPatientIdAndTenantId(patientId,tenantId)
                .orElseThrow(()-> new RuntimeException("Patient's Prescription not found!"));
       List<PrescriptionDto> dto= list.stream().map(PrescriptionDtoMapper::mapToDto).toList();

        return new ResponseEntity<>(dto,HttpStatus.OK);

    }


    public ResponseEntity<PrescriptionDto> getPrescriptionById(Long id, String tenantId, String role) {
        if(!role.equals("ROLE_HOSPITAL") && !role.equals("ROLE_HOSPITAL_USER")){
            throw new RuntimeException("Unauthorized  to retrieve Prescription of patient ");
        }
        Prescription prescription= prescriptionRepository.findByIdAndTenantId(id, tenantId)
                .orElseThrow(()-> new RuntimeException("Prescription not found!"));
        PrescriptionDto dto = PrescriptionDtoMapper.mapToDto(prescription);
        return new ResponseEntity<>(dto,HttpStatus.OK);
    }

    public ResponseEntity<ApiResponse> dispense(Long id, String tenantId, String role) {
        if(!role.equals("ROLE_HOSPITAL") && !role.equals("ROLE_HOSPITAL_USER")){
            throw new RuntimeException("Unauthorized  to retrieve Prescription of patient ");
        }
        Prescription prescription = prescriptionRepository.findByIdAndTenantId(id,tenantId)
                .orElseThrow(()-> new RuntimeException("Prescription not found!"));

        if(prescription.getPrescriptionStatus().equals(PrescriptionStatus.DISPENSED))
            throw new RuntimeException("Already Dispensed");

        prescription.setPrescriptionStatus(PrescriptionStatus.DISPENSED);
        prescriptionRepository.save(prescription);
        return  new ResponseEntity<>(new ApiResponse("prescription dispensed Successfully !!",true), HttpStatus.OK);
    }

    public ResponseEntity<PrescriptionDto> validateForClaim(Long id, String tenantId, String role) {
        if(!role.equals("ROLE_INSURER") && !role.equals("ROLE_INSURER_USER")){
            throw new RuntimeException("Unauthorized to validate for claim ");
        }
        Prescription prescription= prescriptionRepository.findByIdAndTenantId(id,tenantId)
                .orElseThrow(()-> new RuntimeException("Prescription not found!"));
        if(!prescription.getPrescriptionStatus().equals(PrescriptionStatus.ACTIVE)){
            throw new RuntimeException("Prescription is not active");
        }
         boolean expired =prescription
                 .getItemList()
                 .stream().anyMatch(item->item.getExpiryDate().isBefore(LocalDateTime.now()));
        if (expired) {

            throw new RuntimeException("One or more Prescription has expired");
        }
        PrescriptionDto prescriptionDto = PrescriptionDtoMapper.mapToDto(prescription);
        return new ResponseEntity<>(prescriptionDto,HttpStatus.OK);
    }
}
