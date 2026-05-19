package com.ashish.claimbridge.claimservice.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@AllArgsConstructor
@Data
@NoArgsConstructor
public class BillItem {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String itemName;

    private String BillItemCategory;
    private Integer quantity;
    private double unitPrize;
    private double totalPrice;
    private String notes;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "hospital_bill_id")
    private HospitalBill hospitalBill;
}
