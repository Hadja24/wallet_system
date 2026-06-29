package com.badwallet.service;

import com.badwallet.client.PaymentServiceClient;
import com.badwallet.dto.*;
import com.badwallet.model.Transaction;
import com.badwallet.model.Wallet;
import com.badwallet.repository.WalletRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class WalletService {
    private final WalletRepository walletRepository;
    private final PaymentServiceClient paymentServiceClient;
    
    private static final Double MAX_FEE = 5000.0;
    private static final Double FEE_PERCENTAGE = 0.01;

    @Transactional
    public Wallet createWallet(WalletRequest request) {
        if (walletRepository.existsByPhoneNumber(request.getPhoneNumber())) {
            throw new RuntimeException("Wallet already exists");
        }

        Wallet wallet = new Wallet();
        wallet.setPhoneNumber(request.getPhoneNumber());
        wallet.setEmail(request.getEmail());
        wallet.setBalance(request.getInitialBalance() != null ? request.getInitialBalance() : 0.0);
        wallet.setPinCode(request.getPinCode() != null ? request.getPinCode() : generatePin());
        wallet.setCurrency(request.getCurrency() != null ? request.getCurrency() : "XOF");
        wallet.setCreatedAt(LocalDateTime.now());
        wallet.setUpdatedAt(LocalDateTime.now());
        wallet.setTransactions(new ArrayList<>());
        wallet.setTotalDeposits(0.0);
        wallet.setTotalWithdrawals(0.0);
        wallet.setTotalPayments(0.0);

        Wallet saved = walletRepository.save(wallet);
        paymentServiceClient.generateBills(saved.getPhoneNumber());
        return saved;
    }

    public Page<Wallet> getAllWallets(Pageable pageable) {
        return walletRepository.findAll(pageable);
    }

    public Optional<Wallet> getWalletByPhone(String phoneNumber) {
        return walletRepository.findByPhoneNumber(phoneNumber);
    }

    public Double getBalance(String phoneNumber) {
        return walletRepository.findByPhoneNumber(phoneNumber)
            .orElseThrow(() -> new RuntimeException("Wallet not found"))
            .getBalance();
    }

    @Transactional
    public Transaction deposit(DepositRequest request) {
        Wallet wallet = walletRepository.findById(request.getWalletId())
            .orElseThrow(() -> new RuntimeException("Wallet not found"));

        Transaction t = new Transaction();
        t.setId(UUID.randomUUID().toString());
        t.setType("DEPOSIT");
        t.setAmount(request.getAmount());
        t.setFees(0.0);
        t.setNetAmount(request.getAmount());
        t.setDescription("Deposit via " + request.getSource());
        t.setReference(UUID.randomUUID().toString());
        t.setTimestamp(LocalDateTime.now());
        t.setStatus("COMPLETED");
        t.setSourceWallet(wallet.getPhoneNumber());

        wallet.setBalance(wallet.getBalance() + request.getAmount());
        wallet.setTotalDeposits(wallet.getTotalDeposits() + request.getAmount());
        wallet.getTransactions().add(t);
        wallet.setUpdatedAt(LocalDateTime.now());

        walletRepository.save(wallet);
        return t;
    }

    @Transactional
    public Transaction withdraw(WithdrawRequest request) {
        Wallet wallet = walletRepository.findById(request.getWalletId())
            .orElseThrow(() -> new RuntimeException("Wallet not found"));

        Double fees = Math.min(request.getAmount() * FEE_PERCENTAGE, MAX_FEE);
        
        if (wallet.getBalance() < request.getAmount()) {
            throw new RuntimeException("Insufficient balance");
        }

        Transaction t = new Transaction();
        t.setId(UUID.randomUUID().toString());
        t.setType("WITHDRAWAL");
        t.setAmount(request.getAmount());
        t.setFees(fees);
        t.setNetAmount(request.getAmount() - fees);
        t.setDescription("Withdrawal");
        t.setReference(UUID.randomUUID().toString());
        t.setTimestamp(LocalDateTime.now());
        t.setStatus("COMPLETED");
        t.setSourceWallet(wallet.getPhoneNumber());

        wallet.setBalance(wallet.getBalance() - request.getAmount());
        wallet.setTotalWithdrawals(wallet.getTotalWithdrawals() + request.getAmount());
        wallet.getTransactions().add(t);
        wallet.setUpdatedAt(LocalDateTime.now());

        walletRepository.save(wallet);
        return t;
    }

    @Transactional
    public Transaction transfer(TransferRequest request) {
        Wallet source = walletRepository.findById(request.getSourceWalletId())
            .orElseThrow(() -> new RuntimeException("Source wallet not found"));
        Wallet target = walletRepository.findByPhoneNumber(request.getTargetPhoneNumber())
            .orElseThrow(() -> new RuntimeException("Target wallet not found"));

        if (source.getBalance() < request.getAmount()) {
            throw new RuntimeException("Insufficient balance");
        }

        Transaction t = new Transaction();
        t.setId(UUID.randomUUID().toString());
        t.setType("TRANSFER");
        t.setAmount(request.getAmount());
        t.setFees(0.0);
        t.setNetAmount(request.getAmount());
        t.setDescription("Transfer to " + request.getTargetPhoneNumber());
        t.setReference(UUID.randomUUID().toString());
        t.setTimestamp(LocalDateTime.now());
        t.setStatus("COMPLETED");
        t.setSourceWallet(source.getPhoneNumber());
        t.setTargetWallet(target.getPhoneNumber());

        source.setBalance(source.getBalance() - request.getAmount());
        source.getTransactions().add(t);
        source.setUpdatedAt(LocalDateTime.now());

        Transaction ct = new Transaction();
        ct.setId(UUID.randomUUID().toString());
        ct.setType("DEPOSIT");
        ct.setAmount(request.getAmount());
        ct.setFees(0.0);
        ct.setNetAmount(request.getAmount());
        ct.setDescription("Received from " + source.getPhoneNumber());
        ct.setReference(UUID.randomUUID().toString());
        ct.setTimestamp(LocalDateTime.now());
        ct.setStatus("COMPLETED");
        ct.setSourceWallet(source.getPhoneNumber());
        ct.setTargetWallet(target.getPhoneNumber());

        target.setBalance(target.getBalance() + request.getAmount());
        target.getTransactions().add(ct);
        target.setUpdatedAt(LocalDateTime.now());

        walletRepository.save(source);
        walletRepository.save(target);
        return t;
    }

    @Transactional
    public Transaction payInvoice(PayInvoiceRequest request) {
        Wallet wallet = walletRepository.findById(request.getWalletId())
            .orElseThrow(() -> new RuntimeException("Wallet not found"));

        Invoice invoice = paymentServiceClient.getCurrentInvoice(request.getPhoneNumber(), request.getProvider());
        
        if (invoice == null || "PAID".equals(invoice.getStatus())) {
            throw new RuntimeException("No unpaid invoice found");
        }

        if (wallet.getBalance() < invoice.getAmount()) {
            throw new RuntimeException("Insufficient balance");
        }

        Transaction t = new Transaction();
        t.setId(UUID.randomUUID().toString());
        t.setType("PAYMENT");
        t.setAmount(invoice.getAmount());
        t.setFees(0.0);
        t.setNetAmount(invoice.getAmount());
        t.setDescription("Payment to " + request.getProvider());
        t.setReference(UUID.randomUUID().toString());
        t.setTimestamp(LocalDateTime.now());
        t.setStatus("COMPLETED");
        t.setSourceWallet(wallet.getPhoneNumber());
        t.setPaymentProvider(request.getProvider());
        t.setInvoiceReference(invoice.getReference());

        wallet.setBalance(wallet.getBalance() - invoice.getAmount());
        wallet.setTotalPayments(wallet.getTotalPayments() + invoice.getAmount());
        wallet.getTransactions().add(t);
        wallet.setUpdatedAt(LocalDateTime.now());

        paymentServiceClient.markInvoicePaid(invoice.getReference(), t.getReference());
        walletRepository.save(wallet);
        return t;
    }

    @Transactional
    public List<Transaction> payMultipleInvoices(PayMultipleInvoicesRequest request) {
        Wallet wallet = walletRepository.findById(request.getWalletId())
            .orElseThrow(() -> new RuntimeException("Wallet not found"));

        List<Invoice> invoices = paymentServiceClient.getInvoicesByReferences(request.getInvoiceReferences());
        Double total = invoices.stream().mapToDouble(Invoice::getAmount).sum();

        if (wallet.getBalance() < total) {
            throw new RuntimeException("Insufficient balance");
        }

        List<Transaction> transactions = new ArrayList<>();
        for (Invoice invoice : invoices) {
            Transaction t = new Transaction();
            t.setId(UUID.randomUUID().toString());
            t.setType("PAYMENT");
            t.setAmount(invoice.getAmount());
            t.setFees(0.0);
            t.setNetAmount(invoice.getAmount());
            t.setDescription("Payment to " + invoice.getProvider());
            t.setReference(UUID.randomUUID().toString());
            t.setTimestamp(LocalDateTime.now());
            t.setStatus("COMPLETED");
            t.setSourceWallet(wallet.getPhoneNumber());
            t.setPaymentProvider(invoice.getProvider());
            t.setInvoiceReference(invoice.getReference());

            wallet.setBalance(wallet.getBalance() - invoice.getAmount());
            wallet.setTotalPayments(wallet.getTotalPayments() + invoice.getAmount());
            wallet.getTransactions().add(t);
            wallet.setUpdatedAt(LocalDateTime.now());

            paymentServiceClient.markInvoicePaid(invoice.getReference(), t.getReference());
            transactions.add(t);
        }

        walletRepository.save(wallet);
        return transactions;
    }

    public List<Transaction> getTransactionHistory(String phoneNumber) {
        return walletRepository.findByPhoneNumber(phoneNumber)
            .orElseThrow(() -> new RuntimeException("Wallet not found"))
            .getTransactions();
    }

    public List<Wallet> seedWallets() {
        List<Wallet> wallets = new ArrayList<>();
        for (int i = 1; i <= 10; i++) {
            Wallet w = new Wallet();
            w.setPhoneNumber("77" + String.format("%08d", i));
            w.setEmail("user" + i + "@example.com");
            w.setBalance(100000.0 + (i * 10000));
            w.setPinCode(generatePin());
            w.setCurrency("XOF");
            w.setCreatedAt(LocalDateTime.now());
            w.setUpdatedAt(LocalDateTime.now());
            w.setTransactions(new ArrayList<>());
            w.setTotalDeposits(0.0);
            w.setTotalWithdrawals(0.0);
            w.setTotalPayments(0.0);
            
            wallets.add(walletRepository.save(w));
            paymentServiceClient.generateBills(w.getPhoneNumber());
        }
        return wallets;
    }

    private String generatePin() {
        return String.format("%04d", (int)(Math.random() * 10000));
    }
}