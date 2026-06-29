package com.badwallet.model;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class Transaction {
    private String id;
    private String type; // DEPOSIT, WITHDRAWAL, TRANSFER, PAYMENT
    private Double amount;
    private Double fees;
    private Double netAmount;
    private String description;
    private String reference;
    private LocalDateTime timestamp;
    private String status; // PENDING, COMPLETED, FAILED
    private String sourceWallet;
    private String targetWallet;
    private String paymentProvider; // ISM, WOYAFAL
    private String invoiceReference;
}