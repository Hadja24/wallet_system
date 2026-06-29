package com.badwallet.dto;

import lombok.Data;

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