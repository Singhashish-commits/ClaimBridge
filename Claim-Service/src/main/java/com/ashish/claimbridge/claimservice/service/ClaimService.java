package com.ashish.claimbridge.claimservice.service;

import com.ashish.claimbridge.claimservice.dto.*;
import com.ashish.claimbridge.claimservice.event.ClaimEvent;
import com.ashish.claimbridge.claimservice.feignClient.PatientClient;
import com.ashish.claimbridge.claimservice.feignClient.PrescriptionClient;
import com.ashish.claimbridge.claimservice.mapper.ClaimHistoryDtoMapper;
import com.ashish.claimbridge.claimservice.mapper.dtoMapper;
import com.ashish.claimbridge.claimservice.model.*;
import com.ashish.claimbridge.claimservice.repository.ClaimHistoryRepo;
import com.ashish.claimbridge.claimservice.repository.ClaimRepository;
import com.fasterxml.jackson.core.JsonProcessingException;
import feign.FeignException;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
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
    private final OutBoxService outBoxService;

    public ClaimService(ClaimRepository claimRepository, PatientClient patientClient, PrescriptionClient prescriptionClient, KafkaProducerService kafkaProducerService, ClaimHistoryRepo claimHistoryRepo,   OutBoxService outBoxService) {
        this.claimRepository = claimRepository;
        this.patientClient = patientClient;
        this.prescriptionClient = prescriptionClient;
        this.kafkaProducerService = kafkaProducerService;
        this.claimHistoryRepo = claimHistoryRepo;
        this.outBoxService = outBoxService;
    }

    @Transactional
    public ApiResponse submitClaim(ClaimSubmitDto claimSubmitDto, String tenantId, String role, String email) {
        if (!role.equals("ROLE_HOSPITAL") && !role.equals("ROLE_HOSPITAL_USER")) {
            throw new IllegalArgumentException("not Authorized to submit claim");
        }
        boolean billAlreadyExists = claimRepository
                .existsByPrescriptionIdAndStatusNotIn(claimSubmitDto.getPrescriptionId(), List.of(
                        ClaimStatus.REJECTED,
                        ClaimStatus.CANCELLED
                ));
        if (billAlreadyExists) {
            throw new IllegalStateException("A claim has already been submitted for this prescription bill.");
        }
        try {
            patientClient.getPatientById(claimSubmitDto.getPatientId(), tenantId, role);
        } catch (FeignException.NotFound ex) {
            throw new EntityNotFoundException("Patient does not exist in the system.");
        } catch (FeignException ex) {
            throw new RuntimeException("Error communicating with Patient Service: " + ex.getMessage());
        }

        try {
            ResponseEntity<PrescriptionDto> response = prescriptionClient
                    .validateForClaim(claimSubmitDto.getPrescriptionId(), tenantId, "SYSTEM_INTERNAL");
            PrescriptionDto prescriptionDto = response.getBody();
            claimSubmitDto.setPrescriptionStatus(prescriptionDto.getPrescriptionStatus());
        } catch (FeignException.NotFound ex) {
            throw new RuntimeException("Prescription Invalid or Expired for claim Submission");
        } catch (FeignException ex) {
            throw new RuntimeException("Error communicating with Prescription Service: " + ex.getMessage());
        }

        Claim claim = new Claim();
        claim.setClaimNumber(UUID.randomUUID() + "-" + claimSubmitDto.getPatientId() + "-" + claimSubmitDto.getPrescriptionId());
        claim.setPatientId(claimSubmitDto.getPatientId());
        claim.setInsurancePolicyId(claimSubmitDto.getInsurancePolicyId());
        claim.setPrescriptionId(claimSubmitDto.getPrescriptionId());
        if (claimSubmitDto.getTotalClaimAmount() <= 0)
            throw new IllegalStateException("Claim Amount must be greater than 0.");
        claim.setTotalClaimAmount(claimSubmitDto.getTotalClaimAmount());
        claim.setDiagnosis(claimSubmitDto.getDiagnosis());
        claim.setRemarks(claimSubmitDto.getRemarks());
        claim.setInsurerId(claimSubmitDto.getInsurerId());
        claim.setHospitalId(tenantId);
        claim.setPrescriptionStatus(claimSubmitDto.getPrescriptionStatus());
        claim.setStatus(ClaimStatus.SUBMITTED);
        claim.setSubmittedAt(LocalDateTime.now());
        claim.setApprovedAmount(0.0);
        claim.setRejectedAmount(0.0);
        if (claimSubmitDto.getClaimItems() != null) {
            for (var claimItemDto : claimSubmitDto.getClaimItems()) {
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

        saveHistory(savedClaim.getId(), ClaimStatus.DRAFT.toString(), ClaimStatus.SUBMITTED.toString(), email, "ClaimHistory Saved");
        kafkaProducerService.sendClaimSubmittedEvent(buildEvent(savedClaim,"claim-submitted"));
        return new ApiResponse("Claim Submitted Successfully", true);

    }

    public ClaimResponse getClaimById(Long id, String tenantId, String role) {
        Claim claim = claimRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Claim not find with the id " + id));

        if ("ROLE_HOSPITAL".equals(role) || "ROLE_HOSPITAL_USER".equals(role)) {
            if (!claim.getHospitalId().equals(tenantId)) {
                throw new IllegalArgumentException("Unauthorized !!");
            }
        } else if ("ROLE_INSURER".equals(role) || "ROLE_INSURER_USER".equals(role)) {
            if (!claim.getInsurerId().equals(tenantId)) {
                throw new IllegalArgumentException("Unauthorized !!");
            }
        } else {
            throw new IllegalArgumentException("Access Denied: Invalid security role context.");
        }

        ClaimResponse claimResponse = dtoMapper.mapDto(claim);
        return claimResponse;
    }

    public List<ClaimResponse> getClaimByHospitalId(String tenantId, String role) {
        if (!role.equals("ROLE_HOSPITAL") && !role.equals("ROLE_HOSPITAL_USER")) {
            throw new IllegalArgumentException("Unauthorized !!");
        }

        List<Claim> claims = claimRepository.findByHospitalId(tenantId);
        List<ClaimResponse> response = claims.stream()
                .map(claim -> dtoMapper.mapDto(claim))
                .toList();
        return response;
    }

    public List<ClaimResponse> getClaimByStatus(ClaimStatus status, String tenantId, String role) {
        List<Claim> claims;
        if ("ROLE_HOSPITAL".equals(role) || "ROLE_HOSPITAL_USER".equals(role)) {
            claims = claimRepository.findByStatusAndHospitalId(status, tenantId);
        } else if ("ROLE_INSURER".equals(role) || "ROLE_INSURER_USER".equals(role)) {
            claims = claimRepository.findByStatusAndInsurerId(status, tenantId);
        } else {
            throw new IllegalArgumentException("Unauthorized to check claims!! ");
        }

        List<ClaimResponse> response = claims.stream()
                .map(claim -> dtoMapper.mapDto(claim))
                .toList();
        return response;
    }

    @Transactional
    public ApiResponse approveClaim(Long id, ClaimApproveDto dto,
                                    String tenantId, String role, String email) throws JsonProcessingException {
        if (!"ROLE_INSURER".equals(role) && !"ROLE_INSURER_USER".equals(role)) {
            throw new IllegalArgumentException("Unauthorized  to Settle claim !!");
        }
        Claim claim = claimRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Claim not found with the id " + id));
        if (!claim.getInsurerId().equals(tenantId)) {
            throw new IllegalArgumentException("not Authorized to Settle claim !!");
        }
        if (!claim.getStatus().equals(ClaimStatus.SUBMITTED) &&
                !claim.getStatus().equals(ClaimStatus.UNDER_REVIEW) &&
                !claim.getStatus().equals(ClaimStatus.PRE_APPROVED)) {
            throw new IllegalStateException("Claim cant be approved at this stage");
        }
        if (dto.getItemApprovals() != null) {
            for (ClaimItemApproveDto itemDto : dto.getItemApprovals()) {
                ClaimItem item = claim.getClaimItems().stream()
                        .filter(i -> i.getId().equals(itemDto.getClaimItemId()))
                        .findFirst().orElseThrow(() -> new RuntimeException("ClaimItem not found with the id " + itemDto.getClaimItemId()));
                item.setApprovedAmount(itemDto.getApprovedAmount());
                double Rejected = item.getTotalPrice() - item.getApprovedAmount();
                item.setRejectedAmount(Rejected); // rejected Amount
                if (itemDto.getRejectionReason() != null) {
                    item.setRejectionReason(itemDto.getRejectionReason());
                }
            }
        }
        double totalApprove = claim.getClaimItems().stream()
                .mapToDouble(i -> i.getApprovedAmount() != null ? i.getApprovedAmount() : 0.0)
                .sum();
        double safeTotalAmount = claim.getTotalClaimAmount() != null ? claim.getTotalClaimAmount() : 0.0;
        double totalRejected = safeTotalAmount - totalApprove;

        claim.setApprovedAmount(totalApprove);
        claim.setRejectedAmount(totalRejected);

        String existNote = claim.getRemarks() != null ? claim.getRemarks() : "";

        claim.setRemarks(existNote + " APPROVAL REMARK :-" + dto.getRemark());
        String eventType;
        String topic;

        ClaimStatus previousState = claim.getStatus();
        if (totalApprove == 0) {
            claim.setStatus(ClaimStatus.REJECTED);
            topic = "claim.rejected";
            eventType = "claim-rejected";

        } else if (totalRejected > 0) {
            claim.setStatus(ClaimStatus.PARTIALLY_APPROVED);
            topic = "claim.partially-approved";
            eventType = "claim-Partially_Approved";
        } else {
            claim.setStatus(ClaimStatus.APPROVED);
            topic = "claim.approved";
            eventType = "claim-approved";
        }
        claimRepository.save(claim);

        outBoxService.saveOutBoxEvent(eventType, topic, buildEvent(claim, eventType.toLowerCase()));
        saveHistory(claim.getId(), previousState.toString(), claim.getStatus().toString(), email, "Form Submitted to approve");
        return new ApiResponse("Claim Approved Successfully", true);
    }

    @Transactional
    public ApiResponse rejectClaimById(Long id, String tenantId, String role, String reason, String email) {
        if (!"ROLE_INSURER".equals(role) && !"ROLE_INSURER_USER".equals(role)) {
            throw new IllegalArgumentException("Unauthorized  to  Reject Claim  !!");
        }
        Claim claim = claimRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Claim not found with the id " + id));
        if (!claim.getInsurerId().equals(tenantId)) {
            throw new IllegalArgumentException("the claim doesnt belong to this Insurance Company!!");

        }
        if (!claim.getStatus().equals(ClaimStatus.SUBMITTED) &&
                !claim.getStatus().equals(ClaimStatus.UNDER_REVIEW) &&
                !claim.getStatus().equals(ClaimStatus.PRE_APPROVED)) {
            throw new IllegalStateException("Claim can't be rejected at this stage");
        }
        String existNote = claim.getRemarks() != null ? claim.getRemarks() : "";
        claim.setRemarks(existNote + " REJECTION REMARK :-" + reason);
        ClaimStatus oldStatus = claim.getStatus();
        claim.setStatus(ClaimStatus.REJECTED);
        claim.setApprovedAmount(0.0);
        claim.setRejectedAmount(claim.getTotalClaimAmount());
        claimRepository.save(claim);
        saveHistory(id, oldStatus.toString(), ClaimStatus.REJECTED.toString(), email, reason);
        outBoxService.saveOutBoxEvent("claim-rejected","claim.rejected",buildEvent(claim,"claim-Rejected"));
//        kafkaProducerService.sendClaimRejectedEvent(claim);
        return new ApiResponse("Claim Rejected", true);
    }

    @Transactional
    public ApiResponse cancelClaimById(Long id, String tenantId, String role, String reason, String email) {
        if (!"ROLE_HOSPITAL".equals(role) && !"ROLE_HOSPITAL_USER".equals(role)) {
            throw new IllegalArgumentException("Unauthorized  to  Cancel Claim  !!");
        }
        Claim claim = claimRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Claim not found with the id " + id));
        if (!claim.getHospitalId().equals(tenantId)) {
            throw new IllegalArgumentException("the claim doesnt belong to this Hospital !!");
        }
        if (!claim.getStatus().equals(ClaimStatus.DRAFT) && !claim.getStatus().equals(ClaimStatus.SUBMITTED) &&
                !claim.getStatus().equals(ClaimStatus.UNDER_REVIEW)) {
            throw new IllegalArgumentException("Claim cant be cancelled at this stage");
        }
        ClaimStatus oldStatus = claim.getStatus();
        claim.setStatus(ClaimStatus.CANCELLED);
        claim.setApprovedAmount(0.0);
        claim.setRejectedAmount(claim.getTotalClaimAmount());
        String existNote = claim.getRemarks() != null ? claim.getRemarks() : "";
        claim.setRemarks(existNote + " CANCEL REMARK :-" + reason);
        claimRepository.save(claim);
        saveHistory(claim.getId(), oldStatus.toString(), ClaimStatus.CANCELLED.toString(), email, reason);
        outBoxService.saveOutBoxEvent("claim-cancelled","claim.cancelled",buildEvent(claim,"claim-Cancelled"));
//        kafkaProducerService.sendClaimCancelledEvent(claim);
        return new ApiResponse("Claim Cancelled", true);

    }

    public List<ClaimHistoryDto> getClaimHistory(Long id, String tenantId, String role) {
        if (!"ROLE_INSURER".equals(role) && !"ROLE_INSURER_USER".equals(role) && !"ROLE_HOSPITAL".equals(role) && !"ROLE_HOSPITAL_USER".equals(role)) {
            throw new IllegalArgumentException("Unauthorized  to  Get Claim History  !!");
        }


        List<ClaimHistory> historyList = claimHistoryRepo.findByClaimId(id);


        List<ClaimHistoryDto> list =
                historyList.stream()
                        .map(ClaimHistoryDtoMapper::mapDto)
                        .toList();
        return list;
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


    public ClaimStatsDto getClaimStats(String tenantId, String role) {
        List<ClaimStatsSummary> summaries;
        if ("ROLE_HOSPITAL".equals(role) || "ROLE_HOSPITAL_USER".equals(role)) {
            summaries = claimRepository.getHospitalStats(tenantId);
        } else if ("ROLE_INSURER".equals(role) || "ROLE_INSURER_USER".equals(role)) {
            summaries = claimRepository.getInsurerStats(tenantId);
        } else {
            throw new IllegalArgumentException("Unauthorized  to  Get Claim Stats !!");
        }

        ClaimStatsDto claimStatsDto = new ClaimStatsDto();
        if (summaries == null || summaries.isEmpty()) {
            return claimStatsDto;
        }
        for (ClaimStatsSummary summary : summaries) {
            double claimed = summary.getTotalClaimed() != null ? summary.getTotalClaimed() : 0.0;
            double approved = summary.getTotalApproved() != null ? summary.getTotalApproved() : 0.0;
            double rejected = summary.getTotalRejected() != null ? summary.getTotalRejected() : 0.0;

            claimStatsDto.setTotalClaimAmount(claimStatsDto.getTotalClaimAmount() + claimed);
            claimStatsDto.setRejectedClaimAmount(claimStatsDto.getRejectedClaimAmount() + rejected);
            claimStatsDto.setTotalApprovedAmount(claimStatsDto.getTotalApprovedAmount() + approved);

            switch (summary.getStatus()) {
                case SUBMITTED:
                case UNDER_REVIEW:
                case PRE_APPROVED:
                    claimStatsDto.setPendingClaims(claimStatsDto.getPendingClaims() + summary.getCount());
                    break;

                case APPROVED:
                case PARTIALLY_APPROVED:
                    claimStatsDto.setApprovedClaims(claimStatsDto.getApprovedClaims() + summary.getCount());
                    break;
                case CANCELLED:
                case REJECTED:
                    claimStatsDto.setRejectedClaims(claimStatsDto.getRejectedClaims() + summary.getCount());
                    break;
            }
        }
        return claimStatsDto;
    }


    private ClaimEvent buildEvent(Claim claim, String message) {
        ClaimEvent event = new ClaimEvent();
        event.setClaimId(claim.getId());
        event.setClaimNumber(claim.getClaimNumber());
        event.setPatientId(claim.getPatientId());
        event.setHospitalId(claim.getHospitalId());
        event.setInsurerId(claim.getInsurerId());
        event.setStatus(claim.getStatus());
        event.setTotalClaimAmount(claim.getTotalClaimAmount());
        event.setTimestamp(LocalDateTime.now());
        event.setMessage(message);
        event.setPrescriptionId(claim.getPrescriptionId());
        event.setPrescriptionStatus(claim.getPrescriptionStatus());
        event.setApprovedAmount(claim.getApprovedAmount());
        event.setInsurancePolicyId(claim.getInsurancePolicyId());
        ResponseEntity<InsurancePolicyDto> insurancePolicyDto = patientClient.findByIdAndPatientId(claim.getInsurancePolicyId(),
                claim.getPatientId(), "SYSTEM_INTERNAL");
        InsurancePolicyDto dto = insurancePolicyDto.getBody();
        event.setCoverageLimit(dto.getCoverageLimit());
        event.setUsedAmount(dto.getUsedAmount());
        event.setClaimAmount(claim.getTotalClaimAmount());


        return event;
    }



}


