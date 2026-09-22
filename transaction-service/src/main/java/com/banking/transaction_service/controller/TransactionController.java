package com.banking.transaction_service.controller;

import com.banking.transaction_service.dto.TransactionRequest;
import com.banking.transaction_service.dto.TransactionResponse;
import com.banking.transaction_service.service.TransactionService;
import feign.Response;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/transactions")
@RequiredArgsConstructor
@Slf4j
public class TransactionController {
    private final TransactionService transactionService;

    @PostMapping("/transfer")
    public ResponseEntity<TransactionResponse> createTransfer(
            @Valid @RequestBody TransactionRequest request
    ){
        return ResponseEntity.status(HttpStatus.CREATED).body(transactionService.createTransfer(request));
    }

    @GetMapping("/{transactionId}")
    public ResponseEntity<TransactionResponse> getTransaction(
            @PathVariable String transactionId
    ){
        return ResponseEntity.ok(transactionService.getTransaction(transactionId));
    }

    @GetMapping("/account/{accountNumber}")
    public ResponseEntity<List<TransactionResponse>> getTransactionHistory(
            @PathVariable String accountNumber
    ){
        return ResponseEntity.ok(transactionService.getTransactionHistory(accountNumber));
    }

    @PostMapping("/{transactionId}/verify")
    public ResponseEntity<TransactionResponse> verifyOtp(
            @PathVariable String transactionId,
            @PathVariable String otp
    ){
        log.info("OTP verification request - transaction {}", transactionId);
        return ResponseEntity.ok(transactionService.verifyOtp(transactionId , otp));
    }
}
