package com.badwallet.dto;

import lombok.Data;

@Data
public class PayInvoiceRequest {
    private String walletId;
    private String phoneNumber;
    private String provider; // ISM, WOYAFAL
}