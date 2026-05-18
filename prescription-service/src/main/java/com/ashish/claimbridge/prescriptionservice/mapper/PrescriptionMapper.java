package com.ashish.claimbridge.prescriptionservice.mapper;

import com.ashish.claimbridge.prescriptionservice.dto.PrescriptionDto;
import com.ashish.claimbridge.prescriptionservice.dto.PrescriptionItemDto;
import com.ashish.claimbridge.prescriptionservice.model.Prescription;
import com.ashish.claimbridge.prescriptionservice.model.PrescriptionItem;
import com.ashish.claimbridge.prescriptionservice.model.PrescriptionStatus;

import java.util.ArrayList;
import java.util.List;

public class PrescriptionMapper {
    static public Prescription fromDtoToPrescription(PrescriptionDto dto,String tenantId) {
        Prescription prescription = new Prescription();
        prescription.setPatientId(dto.getPatientId());
        prescription.setPrescriptionStatus(PrescriptionStatus.ACTIVE);
        prescription.setDoctorName(dto.getDoctorName());
        prescription.setIssueDate(dto.getIssuedDate());
        prescription.setTenantId(tenantId);
        List<PrescriptionItem> itemList = new ArrayList<>();

        List<PrescriptionItemDto> itemsDto = dto.getItems();
        for(PrescriptionItemDto items : itemsDto) {
            PrescriptionItem item = new PrescriptionItem();
            item.setPrescription(prescription);
            item.setDosage(items.getDosage());
            item.setDrugCode(items.getDrugCode());
            item.setQuantity(items.getQuantity());
            item.setExpiryDate(items.getExpiryDate());
            item.setDrugName(items.getDrugName());
            itemList.add(item);
        }
        prescription.setItemList(itemList);
            return prescription;
    }
}
