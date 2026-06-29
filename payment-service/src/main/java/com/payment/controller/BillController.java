package com.payment.controller;

import com.payment.dto.BillRequest;
import com.payment.model.Bill;
import com.payment.service.BillService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/bills")
@RequiredArgsConstructor
public class BillController {
    private final BillService billService;

    @PostMapping("/generate/{phoneNumber}")
    public ResponseEntity<Void> generate(@PathVariable String phoneNumber) {
        billService.generateMonthlyBills(phoneNumber);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/current/{phoneNumber}")
    public ResponseEntity<Bill> current(@PathVariable String phoneNumber, @RequestParam String provider) {
        Bill bill = billService.getCurrentInvoice(phoneNumber, provider);
        return bill != null ? ResponseEntity.ok(bill) : ResponseEntity.notFound().build();
    }

    @GetMapping("/period/{phoneNumber}")
    public ResponseEntity<List<Bill>> period(@PathVariable String phoneNumber,
                                              @RequestParam String startDate,
                                              @RequestParam String endDate) {
        return ResponseEntity.ok(billService.getInvoicesByPeriod(phoneNumber, startDate, endDate));
    }

    @GetMapping("/references")
    public ResponseEntity<List<Bill>> references(@RequestParam List<String> references) {
        return ResponseEntity.ok(billService.getInvoicesByReferences(references));
    }

    @PutMapping("/{reference}/pay")
    public ResponseEntity<Void> pay(@PathVariable String reference, @RequestParam String transactionReference) {
        billService.markInvoicePaid(reference, transactionReference);
        return ResponseEntity.ok().build();
    }

    @GetMapping
    public ResponseEntity<List<Bill>> list() {
        return ResponseEntity.ok(billService.getAllBills());
    }

    @GetMapping("/phone/{phoneNumber}")
    public ResponseEntity<List<Bill>> byPhone(@PathVariable String phoneNumber) {
        return ResponseEntity.ok(billService.getBillsByPhone(phoneNumber));
    }

    @GetMapping("/unpaid/{phoneNumber}")
    public ResponseEntity<List<Bill>> unpaid(@PathVariable String phoneNumber) {
        return ResponseEntity.ok(billService.getUnpaidBills(phoneNumber));
    }

    @GetMapping("/{reference}")
    public ResponseEntity<Bill> get(@PathVariable String reference) {
        return ResponseEntity.ok(billService.getBillByReference(reference));
    }

    @PostMapping
    public ResponseEntity<Bill> create(@RequestBody BillRequest request) {
        return ResponseEntity.ok(billService.createBill(request));
    }

    @PostMapping("/generate/all")
    public ResponseEntity<Void> generateAll(@RequestBody List<String> phoneNumbers) {
        billService.generateBillsForAllWallets(phoneNumbers);
        return ResponseEntity.ok().build();
    }
}