package com.ashish.claimbridge.prescriptionservice.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.List;

@Data
@Entity
@AllArgsConstructor
@NoArgsConstructor
public class Prescription {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private Long patientId;
    private String doctorName;
    private LocalDate issueDate;
    @Enumerated(EnumType.STRING)
    private PrescriptionStatus prescriptionStatus;
    private String tenantId;  // which hospital submitted
    private String insurerId;

    @OneToMany(mappedBy = "prescription",cascade = CascadeType.ALL)
    private List<PrescriptionItem> itemList;










}
