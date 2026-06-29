package com.badwallet.model;

import jakarta.persistence.*;
import lombok.Data;
import org.hibernate.annotations.GenericGenerator;
import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "transactions")
public class Transaction {
    @Id
    @GeneratedValue(generator = "uuid2")
    @GenericGenerator(name = "uuid2", strategy = "uuid2")
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