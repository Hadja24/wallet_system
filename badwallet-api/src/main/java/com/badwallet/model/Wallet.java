package com.badwallet.model;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import java.time.LocalDateTime;
import java.util.List;

@Data
@Document(collection = "wallets")
public class Wallet {
    @Id
    private String id;
    private String phoneNumber;
    private String email;
    private Double balance;
    private String pinCode;
    private String currency;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private List<Transaction> transactions;
    private Double totalDeposits;
    private Double totalWithdrawals;
    private Double totalPayments;
}