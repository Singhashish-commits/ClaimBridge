package com.ashish.claimbridge.prescriptionservice.dto;

import lombok.*;

import java.time.LocalDate;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class PrescriptionDto {
    private Long patientId;
    private String doctorName;
    private LocalDate issuedDate;
    private String insurerId;
    private String HospitalId;
    private List<PrescriptionItemDto> items;
    private String prescriptionStatus;

}
