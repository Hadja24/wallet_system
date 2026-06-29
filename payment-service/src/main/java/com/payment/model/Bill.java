package com.payment.model;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import java.time.LocalDateTime;

@Data
@Document(collection = "bills")
public class Bill {
    @Id
    private String id;
    private String reference;
    private String phoneNumber;
    private String provider; // ISM, WOYAFAL
    private Double amount;
    private String month;
    private String year;
    private String status; // PAID, UNPAID, OVERDUE
    private String description;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private LocalDateTime paidAt;
    private String transactionReference;
}