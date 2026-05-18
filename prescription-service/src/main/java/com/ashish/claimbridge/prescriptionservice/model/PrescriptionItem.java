package com.ashish.claimbridge.prescriptionservice.model;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.ManyToAny;

import java.time.LocalDateTime;


@Entity
@Table(name = "prescription_items")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class PrescriptionItem {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    private String drugCode;
    private String drugName;
    private LocalDateTime expiryDate;
    private String dosage;
    private String quantity;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "prescription_id")
    @JsonBackReference
    private Prescription prescription;





}
