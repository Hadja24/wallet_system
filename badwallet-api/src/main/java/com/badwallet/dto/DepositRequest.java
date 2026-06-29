package com.badwallet.dto;

import lombok.Data;

@Data
public class DepositRequest {
    private String walletId;
    private Double amount;
    private String source; // CREDIT_CARD, WALLET_TARGET
}