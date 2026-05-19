package com.ashish.claimbridge.claimservice.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Entity
@AllArgsConstructor
@NoArgsConstructor

public class ClaimItem {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    private String itemName;

    @Enumerated(EnumType.STRING)
    private BillItemCategory category; // bill item category and claim item category are same

    private Integer quantity;

    private Double totalPrice;

    private Boolean claimable;

    private Double approvedAmount;

    private Double rejectedAmount;

    private String rejectionReason;

    @Enumerated(EnumType.STRING)
    private ClaimItemStatus status;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "claim_id")
    private Claim claim;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "bill_item_id")
    private BillItem billItem;
}
