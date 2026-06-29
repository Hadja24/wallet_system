package com.badwallet.dto;

import lombok.Data;

@Data
public class WalletRequest {
    private String phoneNumber;
    private String email;
    private Double initialBalance;
    private String pinCode;
    private String currency;
}