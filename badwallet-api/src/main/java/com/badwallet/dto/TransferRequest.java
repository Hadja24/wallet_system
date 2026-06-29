package com.badwallet.dto;

import lombok.Data;

@Data
public class TransferRequest {
    private String sourceWalletId;
    private String targetPhoneNumber;
    private Double amount;
}