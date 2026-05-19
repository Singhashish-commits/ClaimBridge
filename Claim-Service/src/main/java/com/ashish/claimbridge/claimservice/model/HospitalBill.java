package com.ashish.claimbridge.claimservice.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
public class HospitalBill {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    private String billNumber;
    private Long patientId;
    private double totalAmount;
    private LocalDate admissionDate;
    private LocalDate dischargeDate;
    private LocalDateTime createdAt;
    @OneToMany(mappedBy = "hospitalBill",
            cascade = CascadeType.ALL,
            orphanRemoval = true)
    private List<BillItem> billItems = new ArrayList<>();



}
