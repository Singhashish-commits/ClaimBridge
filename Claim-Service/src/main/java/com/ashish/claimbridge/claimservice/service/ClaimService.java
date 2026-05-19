package com.ashish.claimbridge.claimservice.service;

import com.ashish.claimbridge.claimservice.dto.ApiResponse;
import com.ashish.claimbridge.claimservice.dto.ClaimSubmitDto;
import com.ashish.claimbridge.claimservice.feignClient.PatientClient;
import com.ashish.claimbridge.claimservice.feignClient.PrescriptionClient;
import com.ashish.claimbridge.claimservice.model.Claim;
import com.ashish.claimbridge.claimservice.model.ClaimItem;
import com.ashish.claimbridge.claimservice.model.ClaimStatus;
import com.ashish.claimbridge.claimservice.repository.ClaimRepository;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
public class ClaimService {


    private final ClaimRepository claimRepository;
    private final PatientClient patientClient;
    private final PrescriptionClient prescriptionClient;

    public ClaimService(ClaimRepository claimRepository, PatientClient patientClient, PrescriptionClient prescriptionClient) {
        this.claimRepository = claimRepository;
        this.patientClient = patientClient;
        this.prescriptionClient = prescriptionClient;
    }

    public ResponseEntity<ApiResponse> submitClaim(ClaimSubmitDto claimSubmitDto, String tenantId, String role, String email) {
        if(!role.equals("ROLE_HOSPITAL") && !role.equals("ROLE_HOSPITAL_USER")){
            throw new RuntimeException("not Authorized to submit claim");
        }
        boolean billAlreadyExists = claimRepository
                .existsByPrescriptionIdAndStatusNot(claimSubmitDto.getPrescriptionId(),ClaimStatus.REJECTED);
        if (billAlreadyExists) {
            throw new IllegalStateException("A claim has already been submitted for this prescription bill.");
        }
        try{
           patientClient.getPatientById(claimSubmitDto.getPatientId(), tenantId, role);
        }catch(Exception e){
            throw new RuntimeException("patient not find or unAuthorized to get patient for claim Submission");
        }
        try{
            prescriptionClient.validateForClaim(claimSubmitDto.getPrescriptionId(), tenantId, role);
        }catch(Exception e){
            throw new RuntimeException("Prescription Invalid or Expired for claim Submission");
        }
        Claim claim = new Claim();
        claim.setClaimNumber(UUID.randomUUID() + "-" + claimSubmitDto.getPatientId() + "-" + claimSubmitDto.getPrescriptionId());
        claim.setPatientId(claimSubmitDto.getPatientId());
        claim.setInsurancePolicyId(claimSubmitDto.getInsurancePolicyId());
        claim.setPrescriptionId(claimSubmitDto.getPrescriptionId());
        claim.setTotalClaimAmount(claimSubmitDto.getTotalClaimAmount());
        claim.setDiagnosis(claimSubmitDto.getDiagnosis());
        claim.setRemarks(claimSubmitDto.getRemarks());
        claim.setInsurerId(claimSubmitDto.getInsurerId());
        claim.setHospitalId(tenantId);

        claim.setStatus(ClaimStatus.SUBMITTED);
        claim.setSubmittedAt(LocalDateTime.now());
        claim.setApprovedAmount(0.0);
        claim.setRejectedAmount(0.0);

        if(claimSubmitDto.getClaimItems() != null){
            for(var claimItemDto : claimSubmitDto.getClaimItems()){
                ClaimItem claimItem = new ClaimItem();
                claimItem.setItemName(claimItemDto.getItemName());
                claimItem.setCategory(claimItemDto.getBillItemCategory());
                claimItem.setQuantity(claimItemDto.getQuantity());
                claimItem.setTotalPrice(claimItemDto.getPrice());
                claimItem.setClaimable(true);
                claimItem.setApprovedAmount(0.0);
                claimItem.setRejectedAmount(0.0);
                claim.addClaimItem(claimItem);

            }
        }
        Claim savedClaim = claimRepository.save(claim);

        return new ResponseEntity<>( new ApiResponse("Claim Submitted Successfully",true), HttpStatus.OK);

    }

    public ResponseEntity< Claim> getClaimById(Long id, String tenantId, String role){
       Claim claim = claimRepository.findById(id)
               .orElseThrow(()-> new RuntimeException("Claim not find with the id "+id));

       if(role.equals("ROLE_HOSPITAL")||role.equals("ROLE_HOSPITAL_USER")){
           if(!claim.getHospitalId().equals(tenantId)){
                throw new RuntimeException("Unauthorized !!");
           }
       }
       if(!role.equals("ROLE_INSURER")|| role.equals("ROLE_INSURER_USER")){
           if(!claim.getInsurerId().equals(tenantId)){
               throw new RuntimeException("Unauthorized !!");
           }
       }
       return  new ResponseEntity<>(claim, HttpStatus.OK);
    }
    public ResponseEntity<List<Claim>> getClaimByHospitalId(Long id, String tenantId, String role){
        if(!role.equals("ROLE_HOSPITAL") && !role.equals("ROLE_HOSPITAL_USER")){
            throw new RuntimeException("Unauthorized !!");
        }

        List<Claim> claims = claimRepository.findByHospitalId(tenantId)
                .orElseThrow(()-> new RuntimeException("Claim not find ofr the Hospital with Id "+id));
        return   new ResponseEntity<>(claims, HttpStatus.OK);
    }

    public ResponseEntity<List<Claim>> getClaimByStatus(ClaimStatus status, String tenantId, String role){
        List<Claim> claims;
        if(role.equals("ROLE_HOSPITAL")||role.equals("ROLE_HOSPITAL_USER")){
            claims = claimRepository.findByStatusAndHospitalId(status,tenantId)
                    .orElseThrow(()-> new RuntimeException("Claim not find  claims with teh Id "+ tenantId+" and Status"+status));
        }

        else if(role.equals("ROLE_INSURER")|| role.equals("ROLE_INSURER_USER")){
            claims = claimRepository.findByStatusAndInsurerId(status,tenantId)
                    .orElseThrow(()-> new RuntimeException("Claim not find  claims with teh Id "+ tenantId + " and Status"+status));
        }
        else{
            throw new RuntimeException("Unauthorized to check claims!! ");
        }


        return new ResponseEntity<>(claims, HttpStatus.OK);
    }

}
