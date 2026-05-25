package com.ashish.claimbridge.prescriptionservice.mapper;


import com.ashish.claimbridge.prescriptionservice.dto.PrescriptionDto;
import com.ashish.claimbridge.prescriptionservice.dto.PrescriptionItemDto;
import com.ashish.claimbridge.prescriptionservice.model.Drug;
import com.ashish.claimbridge.prescriptionservice.model.Prescription;
import com.ashish.claimbridge.prescriptionservice.model.PrescriptionItem;
import com.ashish.claimbridge.prescriptionservice.repository.DrugRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
@Component
public class PrescriptionDtoMapper {

    public  PrescriptionDto mapToDto(Prescription prescription) {
        PrescriptionDto dto = new PrescriptionDto();
        dto.setPatientId(prescription.getPatientId());
        dto.setDoctorName(prescription.getDoctorName());
        dto.setIssuedDate(prescription.getIssueDate());
        dto.setPrescriptionStatus(prescription.getPrescriptionStatus().toString());
        dto.setItems(mapItem(prescription.getItemList()));
        dto.setInsurerId(prescription.getInsurerId());
        dto.setHospitalId(prescription.getTenantId());

        return dto;
    }
    private   List<PrescriptionItemDto> mapItem(List<PrescriptionItem> items) {
        List<PrescriptionItemDto> dtoList = new ArrayList<>();

        for (PrescriptionItem item : items) {
            PrescriptionItemDto dto = new PrescriptionItemDto();
            dto.setDosage(item.getDosage());
            dto.setQuantity(item.getQuantity());
            dto.setDrugName(item.getDrug().getDrugName());
            dto.setDrugCode(item.getDrug().getDrugCode());
            dto.setExpiryDate(item.getExpiryDate());
            dto.setBilledAmount(item.getBilledAmount());

            dtoList.add(dto);
        }
        return dtoList;
    }
}
