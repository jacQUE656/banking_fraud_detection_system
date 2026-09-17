package com.banking.accounting_service.controller;

import com.banking.accounting_service.dto.AccountResponse;
import com.banking.accounting_service.dto.CreateAccountRequest;
import com.banking.accounting_service.service.AccountService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;

@RestController
@RequestMapping("/api/v1/accounts")
@Slf4j
@RequiredArgsConstructor
public class AccountController {
    private final AccountService  accountService;

    @PostMapping
    public ResponseEntity<AccountResponse> createAccount(
            @Valid @RequestBody CreateAccountRequest request
            ){
        return ResponseEntity.status(HttpStatus.CREATED).body(accountService.createAccount(request));
    }

    @GetMapping("/{accountNumber}")
    public ResponseEntity<AccountResponse> getAccount(
            @PathVariable String accountNumber
    ){
        return ResponseEntity.ok(accountService.getAccount(accountNumber));
    }

    @GetMapping("/{accountNumber}/balance")
    public ResponseEntity<BigDecimal> getAccountBalance(
            @PathVariable String accountNumber
    ){
        return ResponseEntity.ok(accountService.getBalance(accountNumber));
    }

    @PutMapping("/{accountNumber}/block")
    public ResponseEntity<String> blockAccount(
            @PathVariable String accountNumber
    ){
        accountService.blockAccount(accountNumber);
        return ResponseEntity.ok("Account Blocked Successfully");
    }
/**
 *  SAGA STEP 1 - Deduct Balance
 *  Called by transaction service when transfer is initiated
 */
    @PutMapping("/{accountNumber}/deduct")
    public ResponseEntity<String> deductBalance(
            @PathVariable String accountNumber,
            @RequestParam BigDecimal deductAmount
    ){
        accountService.deductBalance(accountNumber,deductAmount);
        return ResponseEntity.ok("Account Deducted Successfully");
    }

    /**
     *  SAGA STEP 2 - Compensating transaction endpoint
     *  cCalled by transaction service in two scenarios
     *  1. Fraud detected -> refund sender (undo step 1)
     *  2. Transaction completed -> Credit receiver
     */

    @PutMapping("/{accountNumber}/credit")
    public ResponseEntity<String> creditBalance(
            @PathVariable String accountNumber,
            @RequestParam BigDecimal creditAmount
    ){
        accountService.creditBalance(accountNumber,creditAmount);
        return ResponseEntity.ok("Account Credit Successfully");
    }

}
