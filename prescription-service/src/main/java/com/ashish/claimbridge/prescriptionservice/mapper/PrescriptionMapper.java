package com.ashish.claimbridge.prescriptionservice.mapper;

import com.ashish.claimbridge.prescriptionservice.dto.PrescriptionDto;
import com.ashish.claimbridge.prescriptionservice.dto.PrescriptionItemDto;
import com.ashish.claimbridge.prescriptionservice.model.Drug;
import com.ashish.claimbridge.prescriptionservice.model.Prescription;
import com.ashish.claimbridge.prescriptionservice.model.PrescriptionItem;
import com.ashish.claimbridge.prescriptionservice.model.PrescriptionStatus;
import com.ashish.claimbridge.prescriptionservice.repository.DrugRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class PrescriptionMapper {
    private final DrugRepository drugRepository;
    @Autowired
    public PrescriptionMapper(DrugRepository drugRepository){
        this.drugRepository= drugRepository;
    }
    public Prescription fromDtoToPrescription(PrescriptionDto dto,String tenantId) {
        Prescription prescription = new Prescription();
        prescription.setPatientId(dto.getPatientId());
        prescription.setPrescriptionStatus(PrescriptionStatus.ACTIVE);
        prescription.setDoctorName(dto.getDoctorName());
        prescription.setIssueDate(dto.getIssuedDate());
        prescription.setTenantId(tenantId);
        prescription.setInsurerId(dto.getInsurerId());
        List<PrescriptionItem> itemList = new ArrayList<>();

        List<PrescriptionItemDto> itemsDto = dto.getItems();
        for(PrescriptionItemDto items : itemsDto) {
            PrescriptionItem item = new PrescriptionItem();
            item.setPrescription(prescription);
            item.setDosage(items.getDosage());
            item.setBilledAmount(items.getBilledAmount());
            item.setQuantity(items.getQuantity());
            item.setExpiryDate(items.getExpiryDate());
            Drug drug = drugRepository.findByDrugCode(items.getDrugCode())
                    .orElseThrow(()-> new EntityNotFoundException("Drug not found: " + items.getDrugCode()));
            item.setDrug(drug);
            itemList.add(item);

        }
        prescription.setItemList(itemList);
            return prescription;
    }
}
