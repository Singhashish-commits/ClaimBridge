package com.ashish.claimbridge.claimservice.mapper;

import com.ashish.claimbridge.claimservice.dto.ClaimItemResponseDto;
import com.ashish.claimbridge.claimservice.dto.ClaimResponse;
import com.ashish.claimbridge.claimservice.model.Claim;
import com.ashish.claimbridge.claimservice.model.ClaimItem;

import java.util.ArrayList;
import java.util.List;

public class dtoMapper {
    static public ClaimResponse mapDto(Claim claim){
        if(claim==null){
            return null;
        }
        ClaimResponse claimResponse = new ClaimResponse();
        claimResponse.setId(claim.getId());
        claimResponse.setPatientId(claim.getPatientId());
        claimResponse.setClaimNumber(claim.getClaimNumber());
        claimResponse.setDiagnosis(claim.getDiagnosis());
        claimResponse.setApprovedAmount(claim.getApprovedAmount());
        claimResponse.setHospitalId(claim.getHospitalId());
        claimResponse.setInsurerId(claim.getInsurerId());
        claimResponse.setRemarks(claim.getRemarks());
        claimResponse.setSubmittedAt(claim.getSubmittedAt());
        claimResponse.setInsurancePolicyId(claim.getInsurancePolicyId());
        claimResponse.setRejectedAmount(claim.getRejectedAmount());
        claimResponse.setTotalClaimAmount(claim.getTotalClaimAmount());
        claimResponse.setRemarks(claim.getRemarks());
        claimResponse.setSubmittedAt(claim.getSubmittedAt());
        claimResponse.setPrescriptionId(claim.getPrescriptionId());
        claimResponse.setStatus(claim.getStatus());
        List<ClaimItem> items = claim.getClaimItems();
        List<ClaimItemResponseDto> totalClaimItem = new ArrayList<>();
        for (ClaimItem claimItem : items) {
            ClaimItemResponseDto dto = new ClaimItemResponseDto();
            dto.setItemName(claimItem.getItemName());
            dto.setRejectionReason(claimItem.getRejectionReason());
            dto.setRejectedAmount(claimItem.getRejectedAmount());
            dto.setApprovedAmount(claimItem.getApprovedAmount());
            dto.setClaimable(claimItem.getClaimable());
            dto.setQuantity(claimItem.getQuantity());
            dto.setRejectionReason(claimItem.getRejectionReason());
            dto.setTotalPrice(claimItem.getTotalPrice());
            totalClaimItem.add(dto);
        }
        claimResponse.setClaimItems(totalClaimItem);

        return claimResponse;


    }
}
