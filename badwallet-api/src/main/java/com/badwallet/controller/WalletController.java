package com.badwallet.controller;

import com.badwallet.client.PaymentServiceClient;
import com.badwallet.dto.*;
import com.badwallet.model.Transaction;
import com.badwallet.model.Wallet;
import com.badwallet.service.WalletService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/wallets")
@RequiredArgsConstructor
public class WalletController {
    private final WalletService walletService;
    private final PaymentServiceClient paymentServiceClient;

    @PostMapping("/seed")
    public ResponseEntity<List<Wallet>> seed() {
        return ResponseEntity.ok(walletService.seedWallets());
    }

    @PostMapping
    public ResponseEntity<Wallet> create(@RequestBody WalletRequest request) {
        return ResponseEntity.ok(walletService.createWallet(request));
    }

    @GetMapping
    public ResponseEntity<Page<Wallet>> list(Pageable pageable) {
        return ResponseEntity.ok(walletService.getAllWallets(pageable));
    }

    @GetMapping("/{phone}")
    public ResponseEntity<Wallet> get(@PathVariable String phone) {
        return walletService.getWalletByPhone(phone)
            .map(ResponseEntity::ok)
            .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/{phone}/balance")
    public ResponseEntity<Double> balance(@PathVariable String phone) {
        return ResponseEntity.ok(walletService.getBalance(phone));
    }

    @PostMapping("/{id}/deposit")
    public ResponseEntity<Transaction> deposit(@PathVariable String id, @RequestBody DepositRequest request) {
        request.setWalletId(id);
        return ResponseEntity.ok(walletService.deposit(request));
    }

    @PostMapping("/withdraw")
    public ResponseEntity<Transaction> withdraw(@RequestBody WithdrawRequest request) {
        return ResponseEntity.ok(walletService.withdraw(request));
    }

    @PostMapping("/transfer")
    public ResponseEntity<Transaction> transfer(@RequestBody TransferRequest request) {
        return ResponseEntity.ok(walletService.transfer(request));
    }

    @PostMapping("/pay")
    public ResponseEntity<Transaction> pay(@RequestBody PayInvoiceRequest request) {
        return ResponseEntity.ok(walletService.payInvoice(request));
    }

    @PostMapping("/pay-factures")
    public ResponseEntity<List<Transaction>> payMultiple(@RequestBody PayMultipleInvoicesRequest request) {
        return ResponseEntity.ok(walletService.payMultipleInvoices(request));
    }

    @GetMapping("/{phone}/transactions")
    public ResponseEntity<List<Transaction>> history(@PathVariable String phone) {
        return ResponseEntity.ok(walletService.getTransactionHistory(phone));
    }

    @GetMapping("/external/factures/{code}/current")
    public ResponseEntity<Invoice> currentInvoice(@PathVariable String code, @RequestParam String provider) {
        return ResponseEntity.ok(paymentServiceClient.getCurrentInvoice(code, provider));
    }

    @GetMapping("/external/factures/{code}/periode")
    public ResponseEntity<List<Invoice>> periodInvoices(@PathVariable String code,
                                                          @RequestParam String debut,
                                                          @RequestParam String fin) {
        return ResponseEntity.ok(paymentServiceClient.getInvoicesByPeriod(code, debut, fin));
    }
}