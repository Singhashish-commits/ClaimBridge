package com.ashish.claimbridge.patientservice.mapper;

import com.ashish.claimbridge.patientservice.dto.InsurancePolicyDto;
import com.ashish.claimbridge.patientservice.model.InsurencePolicy;

public class InsurancePolicyDtoMapper {
    static public InsurancePolicyDto mapDto(InsurencePolicy policy){
        InsurancePolicyDto dto = new InsurancePolicyDto();
        dto.setPolicyNumber(policy.getPolicyNumber());
        dto.setPatientId(policy.getPatientId());
        dto.setInsurerId(policy.getInsurerId());
        dto.setPolicyNumber(policy.getPolicyNumber());
        dto.setCoverageLimit(policy.getCoverageLimit());
        dto.setUsedAmount(policy.getUsedAmount());
        dto.setValidFrom(policy.getValidFrom());
        dto.setValidTo(policy.getValidTo());
        dto.setStatus(policy.getStatus());
        return dto;
    }
}
