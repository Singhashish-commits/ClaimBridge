package com.ashish.claimbridge.prescriptionservice.mapper;


import com.ashish.claimbridge.prescriptionservice.dto.PrescriptionDto;
import com.ashish.claimbridge.prescriptionservice.dto.PrescriptionItemDto;
import com.ashish.claimbridge.prescriptionservice.model.Prescription;
import com.ashish.claimbridge.prescriptionservice.model.PrescriptionItem;

import java.util.ArrayList;
import java.util.List;

public class PrescriptionDtoMapper {
    public static PrescriptionDto mapToDto(Prescription prescription) {

        PrescriptionDto dto = new PrescriptionDto();

        dto.setPatientId(prescription.getPatientId());
        dto.setDoctorName(prescription.getDoctorName());
        dto.setIssuedDate(prescription.getIssueDate());

        dto.setItems(mapItem(prescription.getItemList()));

        return dto;
    }

    public static List<PrescriptionItemDto> mapItem(List<PrescriptionItem> items) {

        List<PrescriptionItemDto> dtoList = new ArrayList<>();

        for (PrescriptionItem item : items) {

            PrescriptionItemDto dto = new PrescriptionItemDto();

            dto.setDosage(item.getDosage());
            dto.setQuantity(item.getQuantity());
            dto.setDrugName(item.getDrugName());
            dto.setDrugCode(item.getDrugCode());
            dto.setExpiryDate(item.getExpiryDate());

            dtoList.add(dto);
        }

        return dtoList;
    }
}
