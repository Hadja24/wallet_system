package com.badwallet.client;

import com.badwallet.dto.Invoice;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@FeignClient(name = "payment-service", url = "http://localhost:8081")
public interface PaymentServiceClient {
    
    @PostMapping("/api/bills/generate/{phoneNumber}")
    void generateBills(@PathVariable String phoneNumber);
    
    @GetMapping("/api/bills/current/{phoneNumber}")
    Invoice getCurrentInvoice(@PathVariable String phoneNumber, @RequestParam String provider);
    
    @GetMapping("/api/bills/period/{phoneNumber}")
    List<Invoice> getInvoicesByPeriod(@PathVariable String phoneNumber, 
                                       @RequestParam String startDate, 
                                       @RequestParam String endDate);
    
    @GetMapping("/api/bills/references")
    List<Invoice> getInvoicesByReferences(@RequestParam List<String> references);
    
    @PutMapping("/api/bills/{reference}/pay")
    void markInvoicePaid(@PathVariable String reference, @RequestParam String transactionReference);
}