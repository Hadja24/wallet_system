package com.badwallet.dto;

import lombok.Data;
import java.util.List;

@Data
public class PayMultipleInvoicesRequest {
    private String walletId;
    private List<String> invoiceReferences;
}