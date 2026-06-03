package com.ashish.claimbridge.patientservice.mapper;

import com.ashish.claimbridge.patientservice.dto.InsurerDto;
import com.ashish.claimbridge.patientservice.model.Insurer;

import java.util.ArrayList;
import java.util.List;

public class InsurerDtoMapper {
    static public InsurerDto mapDto(Insurer  insurer) {
            InsurerDto insurerDto = new InsurerDto();
            insurerDto.setInsurerName(insurer.getInsurerName());
            insurerDto.setInsurerEmail(insurer.getInsurerEmail());
            insurerDto.setInsurerId(insurer.getTenantId());



        return insurerDto;
    }
}
