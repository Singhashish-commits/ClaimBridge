package com.ashish.claimbridge.claimservice.service;

import com.ashish.claimbridge.claimservice.dto.*;
import com.ashish.claimbridge.claimservice.feignClient.PatientClient;
import com.ashish.claimbridge.claimservice.feignClient.PrescriptionClient;
import com.ashish.claimbridge.claimservice.mapper.ClaimHistoryDtoMapper;
import com.ashish.claimbridge.claimservice.mapper.dtoMapper;
import com.ashish.claimbridge.claimservice.model.Claim;
import com.ashish.claimbridge.claimservice.model.ClaimHistory;
import com.ashish.claimbridge.claimservice.model.ClaimItem;
import com.ashish.claimbridge.claimservice.model.ClaimStatus;
import com.ashish.claimbridge.claimservice.repository.ClaimHistoryRepo;
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
    private final KafkaProducerService kafkaProducerService;
    private final ClaimHistoryRepo claimHistoryRepo;

    public ClaimService(ClaimRepository claimRepository, PatientClient patientClient, PrescriptionClient prescriptionClient, KafkaProducerService kafkaProducerService, ClaimHistoryRepo claimHistoryRepo) {
        this.claimRepository = claimRepository;
        this.patientClient = patientClient;
        this.prescriptionClient = prescriptionClient;
        this.kafkaProducerService = kafkaProducerService;
        this.claimHistoryRepo = claimHistoryRepo;
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
        kafkaProducerService.sendFraudCheckEvent(
                savedClaim.getId(),
                savedClaim.getPatientId(),
                savedClaim.getTotalClaimAmount(),
                tenantId
        );
        saveHistory(savedClaim.getId(),ClaimStatus.DRAFT.toString(),ClaimStatus.SUBMITTED.toString(),email,"ClaimHistory Saved");

        return new ResponseEntity<>( new ApiResponse("Claim Submitted Successfully",true), HttpStatus.OK);

    }

    public ResponseEntity<ClaimResponse> getClaimById(Long id, String tenantId, String role){
       Claim claim = claimRepository.findById(id)
               .orElseThrow(()-> new RuntimeException("Claim not find with the id "+id));

       if(role.equals("ROLE_HOSPITAL") || role.equals("ROLE_HOSPITAL_USER")){
           if(!claim.getHospitalId().equals(tenantId)){
                throw new RuntimeException("Unauthorized !!");
           }
       }
       if(role.equals("ROLE_INSURER")|| role.equals("ROLE_INSURER_USER")){
           if(!claim.getInsurerId().equals(tenantId)){
               throw new RuntimeException("Unauthorized !!");
           }
       }
       ClaimResponse claimResponse = dtoMapper.mapDto(claim);
       return  new ResponseEntity<>(claimResponse, HttpStatus.OK);
    }
    public ResponseEntity<List<ClaimResponse>> getClaimByHospitalId(Long id, String tenantId, String role){
        if(!role.equals("ROLE_HOSPITAL") && !role.equals("ROLE_HOSPITAL_USER")){
            throw new RuntimeException("Unauthorized !!");
        }

        List<Claim> claims = claimRepository.findByHospitalId(tenantId)
                .orElseThrow(()-> new RuntimeException("Claim not find ofr the Hospital with Id "+id));

        List<ClaimResponse> response = claims.stream()
                .map(claim -> dtoMapper.mapDto(claim))
                .toList();
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    public ResponseEntity<List<ClaimResponse>> getClaimByStatus(ClaimStatus status, String tenantId, String role){
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

        List<ClaimResponse> response = claims.stream()
                .map(claim -> dtoMapper.mapDto(claim))
                .toList();
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    public ResponseEntity<ApiResponse> approveClaim( Long id, ClaimApproveDto dto,
                                                     String tenantId, String role,String email){
        if(!role.equals("ROLE_INSURER")&& !role.equals("ROLE_INSURER_USER")){
            throw new RuntimeException("Unauthorized  to Settle claim !!");
        }
        Claim claim = claimRepository.findById(id)
                .orElseThrow(()-> new RuntimeException("Claim not found with the id "+id));
        if(!claim.getInsurerId().equals(tenantId)){
            throw new RuntimeException("not Authorized to Settle claim !!");
        }
        if(!claim.getStatus().equals(ClaimStatus.SUBMITTED)){
            throw new RuntimeException("Claim cant be approved at this stage");
        }
        if(dto.getItemApprovals()!=null){
            for(ClaimItemApproveDto itemDto : dto.getItemApprovals()){
                ClaimItem item= claim.getClaimItems().stream()
                        .filter(i->i.getId().equals(itemDto.getClaimItemId()))
                        .findFirst().orElseThrow(()-> new RuntimeException("ClaimItem not found with the id "+itemDto.getClaimItemId()));
                item.setApprovedAmount(itemDto.getApprovedAmount()); // approved Amount
                double Rejected = item.getTotalPrice()-item.getApprovedAmount();
                item.setRejectedAmount(Rejected); // rejected Amount
                if(itemDto.getRejectionReason()!=null){
                    item.setRejectionReason(item.getRejectionReason());
                }
            }
        }
        double totalApprove = claim.getClaimItems().stream()
                .mapToDouble(i-> i.getApprovedAmount()!=null? i.getApprovedAmount():0.0 )
                .sum();
        double totalRejected = claim.getTotalClaimAmount()-totalApprove;

        claim.setApprovedAmount(totalApprove);
        claim.setRejectedAmount(totalRejected);
        claim.setRemarks(claim.getRemarks());
        if(totalRejected>0){
            claim.setStatus(ClaimStatus.PARTIALLY_APPROVED);
        }
        else{
            claim.setStatus(ClaimStatus.APPROVED);
        }
        claimRepository.save(claim);
        saveHistory(claim.getId(),ClaimStatus.SUBMITTED.toString(), ClaimStatus.APPROVED.toString(),email,"Form Submitted to approve");
        return new ResponseEntity<>(new ApiResponse("Claim Approved Successfully",true), HttpStatus.OK);
    }


    public ResponseEntity<ApiResponse> rejectClaimById(Long id, String tenantId, String role,String reason){
        if(!role.equals("ROLE_INSURER") &&  !role.equals("ROLE_INSURER_USER")){
            throw new RuntimeException("Unauthorized  to  Reject Claim  !!");
        }
        Claim claim = claimRepository.findById(id)
                .orElseThrow(()-> new RuntimeException("Claim not found with the id "+id));
        if(!claim.getInsurerId().equals(tenantId)){
            throw new RuntimeException("the claim doesnt belong to this Insurance Company!!");

        }
        if(!claim.getStatus().equals(ClaimStatus.SUBMITTED) &&
                !claim.getStatus().equals(ClaimStatus.UNDER_REVIEW) &&
                !claim.getStatus().equals(ClaimStatus.PRE_APPROVED)){
           throw new RuntimeException("Claim can't be rejected at this stage");
        }
        claim.setRemarks(reason);
        claim.setStatus(ClaimStatus.REJECTED);
        claim.setApprovedAmount(0.0);
        claim.setRejectedAmount(claim.getTotalClaimAmount());
        claimRepository.save(claim);
        return new ResponseEntity<>(new ApiResponse("Claim Rejected",true), HttpStatus.OK);
    }


    public ResponseEntity<ApiResponse>cancelClaimById( Long id, String tenantId, String role,String reason){
        if(!role.equals("ROLE_HOSPITAL") && !role.equals("ROLE_HOSPITAL_USER")){
            throw new RuntimeException("Unauthorized  to  Cancel Claim  !!");
        }
        Claim claim = claimRepository.findById(id)
                .orElseThrow(()-> new RuntimeException("Claim not found with the id "+id));
        if(!claim.getHospitalId().equals(tenantId)){
            throw new RuntimeException("the claim doesnt belong to this Hospital !!");
        }
        if(!claim.getStatus().equals(ClaimStatus.DRAFT) && !claim.getStatus().equals(ClaimStatus.SUBMITTED) &&
        !claim.getStatus().equals(ClaimStatus.UNDER_REVIEW)){
            throw new RuntimeException("Claim cant be cancelled at this stage");
        }
        claim.setStatus(ClaimStatus.CANCELLED);
        claim.setApprovedAmount(0.0);
        claim.setRejectedAmount(claim.getTotalClaimAmount());
        claim.setRemarks(reason);
        claimRepository.save(claim);
        return new ResponseEntity<>(new ApiResponse("Claim Cancelled",true), HttpStatus.OK);

    }

    public ResponseEntity<List<ClaimHistoryDto>> getClaimHistory(Long id, String tenantId, String role){
        List<ClaimHistory> historyList= claimHistoryRepo.findByClaimId(id)
                .orElseThrow(()->new RuntimeException("Claim History  not found"));


        List<ClaimHistoryDto> list =
                historyList.stream()
                        .map(ClaimHistoryDtoMapper::mapdto)
                        .toList();
        return  new ResponseEntity<>(list,HttpStatus.OK);
    }


    private void saveHistory(Long claimId, String from, String to, String by, String notes) {
        ClaimHistory history = new ClaimHistory();
        history.setClaimId(claimId);
        history.setFromState(from);
        history.setToState(to);
        history.setTriggeredBy(by);
        history.setNotes(notes);
        history.setTimestamp(LocalDateTime.now());
        claimHistoryRepo.save(history);
    }



}



