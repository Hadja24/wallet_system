package com.badwallet.dto;

import lombok.Data;
import java.util.List;

@Data
public class WalletRequest {
    private String phoneNumber;
    private String email;
    private Double initialBalance;
    private String pinCode;
    private String currency;
}

@Data
public class DepositRequest {
    private String walletId;
    private Double amount;
    private String source; // CREDIT_CARD, WALLET_TARGET
}

@Data
public class WithdrawRequest {
    private String walletId;
    private Double amount;
}

@Data
public class TransferRequest {
    private String sourceWalletId;
    private String targetPhoneNumber;
    private Double amount;
}

@Data
public class PayInvoiceRequest {
    private String walletId;
    private String phoneNumber;
    private String provider; // ISM, WOYAFAL
}

@Data
public class PayMultipleInvoicesRequest {
    private String walletId;
    private List<String> invoiceReferences;
}

@Data
public class Invoice {
    private String reference;
    private String phoneNumber;
    private String provider;
    private Double amount;
    private String month;
    private String year;
    private String status; // PAID, UNPAID
    private String description;
}