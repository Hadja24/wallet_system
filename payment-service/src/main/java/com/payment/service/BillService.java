package com.payment.service;

import com.payment.dto.BillRequest;
import com.payment.model.Bill;
import com.payment.repository.BillRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class BillService {
    private final BillRepository billRepository;
    private static final Double ISM_FEE = 50000.0;
    private static final Double WOYAFAL_FEE = 35000.0;
    private static final Double SENELEC_FEE = 25000.0;  
    private static final Double RAPIDO_FEE = 15000.0;  

    @Transactional
    public void generateMonthlyBills(String phoneNumber) {
        LocalDateTime now = LocalDateTime.now();
        for (int i = 0; i < 6; i++) {
            LocalDateTime date = now.minusMonths(i);
            String month = date.format(DateTimeFormatter.ofPattern("MMMM"));
            String year = date.format(DateTimeFormatter.ofPattern("yyyy"));
            
            generateBill(phoneNumber, "ISM", ISM_FEE, month, year);
            generateBill(phoneNumber, "WOYAFAL", WOYAFAL_FEE, month, year);
            generateBill(phoneNumber, "SENELEC", SENELEC_FEE, month, year);
            generateBill(phoneNumber, "RAPIDO", RAPIDO_FEE, month, year);
        }
    }

    private void generateBill(String phone, String provider, Double amount, String month, String year) {
        List<Bill> existing = billRepository.findByPhoneNumberAndProviderAndMonthAndYear(phone, provider, month, year);
        if (existing.isEmpty()) {
            Bill bill = new Bill();
            bill.setReference("BILL-" + provider + "-" + phone + "-" + year + "-" + month);
            bill.setPhoneNumber(phone);
            bill.setProvider(provider);
            bill.setAmount(amount);
            bill.setMonth(month);
            bill.setYear(year);
            bill.setStatus("UNPAID");
            bill.setDescription(provider + " bill for " + month + " " + year);
            bill.setCreatedAt(LocalDateTime.now());
            bill.setUpdatedAt(LocalDateTime.now());
            billRepository.save(bill);
        }
    }

    public Bill getCurrentInvoice(String phoneNumber, String provider) {
        String month = LocalDateTime.now().format(DateTimeFormatter.ofPattern("MMMM"));
        String year = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy"));
        List<Bill> bills = billRepository.findByPhoneNumberAndProviderAndMonthAndYear(phoneNumber, provider, month, year);
        return bills.isEmpty() ? null : bills.get(0);
    }

    public List<Bill> getInvoicesByPeriod(String phoneNumber, String startDate, String endDate) {
        return billRepository.findByPhoneNumber(phoneNumber).stream()
            .filter(b -> "UNPAID".equals(b.getStatus()))
            .toList();
    }

    public List<Bill> getInvoicesByReferences(List<String> references) {
        return billRepository.findByReferenceIn(references);
    }

    @Transactional
    public void markInvoicePaid(String reference, String transactionReference) {
        Bill bill = billRepository.findByReference(reference)
            .orElseThrow(() -> new RuntimeException("Bill not found"));
        bill.setStatus("PAID");
        bill.setPaidAt(LocalDateTime.now());
        bill.setTransactionReference(transactionReference);
        bill.setUpdatedAt(LocalDateTime.now());
        billRepository.save(bill);
    }

    public List<Bill> getAllBills() {
        return billRepository.findAll();
    }

    public List<Bill> getBillsByPhone(String phoneNumber) {
        return billRepository.findByPhoneNumber(phoneNumber);
    }

    public List<Bill> getUnpaidBills(String phoneNumber) {
        return billRepository.findByPhoneNumberAndStatus(phoneNumber, "UNPAID");
    }

    public Bill getBillByReference(String reference) {
        return billRepository.findByReference(reference)
            .orElseThrow(() -> new RuntimeException("Bill not found"));
    }

    public void generateBillsForAllWallets(List<String> phoneNumbers) {
        phoneNumbers.forEach(this::generateMonthlyBills);
    }

    @Transactional
    public Bill createBill(BillRequest request) {
        Bill bill = new Bill();
        bill.setReference("BILL-" + request.getProvider() + "-" + request.getPhoneNumber() + "-" + System.currentTimeMillis());
        bill.setPhoneNumber(request.getPhoneNumber());
        bill.setProvider(request.getProvider());
        bill.setAmount(request.getAmount());
        bill.setMonth(request.getMonth());
        bill.setYear(request.getYear());
        bill.setStatus("UNPAID");
        bill.setDescription(request.getDescription());
        bill.setCreatedAt(LocalDateTime.now());
        bill.setUpdatedAt(LocalDateTime.now());
        return billRepository.save(bill);
    }
}