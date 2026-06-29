package com.payment.dto;

import lombok.Data;

@Data
public class BillRequest {
    private String phoneNumber;
    private String provider;
    private Double amount;
    private String month;
    private String year;
    private String description;
}