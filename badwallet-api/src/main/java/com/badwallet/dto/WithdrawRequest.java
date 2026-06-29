package com.badwallet.dto;

import lombok.Data;

@Data
public class WithdrawRequest {
    private String walletId;
    private Double amount;
}