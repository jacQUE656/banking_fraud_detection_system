package com.banking.accounting_service.service;

import com.banking.accounting_service.dto.AccountResponse;
import com.banking.accounting_service.dto.CreateAccountRequest;
import com.banking.accounting_service.entity.Account;
import com.banking.accounting_service.enums.AccountStatus;
import com.banking.accounting_service.enums.AccountType;
import com.banking.accounting_service.repository.AccountRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.security.SecureRandom;


@Service
@Slf4j
@RequiredArgsConstructor
public class AccountService {
    private final AccountRepository accountRepository;
    public static SecureRandom secureRandom = new SecureRandom();

    //Map to account response
    private AccountResponse mapToAccountResponse(Account account) {
        AccountResponse accountResponse = new AccountResponse();
        accountResponse.setId(account.getId());
        accountResponse.setAccountNumber(account.getAccountNumber());
        accountResponse.setAccountHolderName(account.getAccountHolderName());
        accountResponse.setEmail(account.getEmail());
        accountResponse.setPhone(account.getPhone());
        accountResponse.setAccountType(account.getAccountType());
        accountResponse.setAccountStatus(account.getAccountStatus());
        account.setDailyTransactionLimit(account.getDailyTransactionLimit());
        accountResponse.setBalance(account.getBalance());
        accountResponse.setCreatedAt(account.getCreatedAt());
        return accountResponse;


    }

    //GENERATE 12 DIGIT UNIQUE ACCOUNT NUMBER
    private String generateAccountNumber() {
        String accountNumber;
        do{
            long number = secureRandom.nextLong(1_000_000_000_000L);
            accountNumber = String.format("%012d", number);
        }while (accountRepository.existsByAccountNumber(accountNumber));
        return accountNumber;
    }

    public AccountResponse createAccount(CreateAccountRequest request) {
        log.info("Creating account for  {}", request.getEmail());
        if (accountRepository.existsByEmail(request.getEmail())) {
            throw new RuntimeException("account already exists");
        }
        Account account = Account.builder()
                .accountHolderName(request.getAccountHolderName())
                .email(request.getEmail())
                .phone(request.getPhone())
                .accountType(request.getAccountType())
                .accountStatus(AccountStatus.ACTIVE)
                .balance(request.getInitialDeposit())
                .accountNumber(generateAccountNumber())
                .dailyTransactionLimit(
                     request.getAccountType() == AccountType.SAVINGS ?
                             new BigDecimal("1000000")
                             : new BigDecimal("500000")
                )
                .build();
        Account createdAccount = accountRepository.save(account);
        log.info("Created account for  {}", createdAccount.getAccountNumber());
        return mapToAccountResponse(createdAccount);
    }

    public AccountResponse getAccount(String accountNumber) {
        log.info("Getting account for  {}", accountNumber);
        Account account = accountRepository.findByAccountNumber(accountNumber).orElseThrow(()-> new RuntimeException("account not found"));
        return mapToAccountResponse(account);
    }

    /**
     *
     * @param accountNumber
     * @ get accountBalance
     */

    public BigDecimal getBalance(String accountNumber) {
        log.info("Getting balance for  {}", accountNumber);
        Account account = accountRepository.findByAccountNumber(accountNumber).orElseThrow(()-> new RuntimeException("account not found"));
        return account.getBalance();
    }

    /**
     *
     * @param accountNumber
     * Block account - called by fraud detection service
     */
    public void blockAccount(String accountNumber) {
        log.info("Blocking account for  {}", accountNumber);
        Account account = accountRepository.findByAccountNumber(accountNumber).orElseThrow(()-> new RuntimeException("account not found"));
        account.setAccountStatus(AccountStatus.BLOCKED);
        accountRepository.save(account);
        log.info("Blocked account for  {}", account.getAccountNumber());
    }

    /**
     * deduct balance from sender account called by transaction service
     * @param accountNumber
     * @param deductAmount
     */
    public void deductBalance(String accountNumber, BigDecimal deductAmount) {
        log.info("Deducting account for  {}", accountNumber);
        Account account = accountRepository.findByAccountNumber(accountNumber).orElseThrow(()-> new RuntimeException("account not found"));

        if (account.getAccountStatus() != AccountStatus.ACTIVE) {
            throw new RuntimeException("account status not active");
        }
        if (account.getBalance().compareTo(deductAmount) < 0) {
            throw new RuntimeException("account balance too small");
        }

        account.setBalance(account.getBalance().subtract(deductAmount));
        accountRepository.save(account);
        log.info("Deducted account for  {}", account.getAccountNumber());
    }

    /**
     * credit balance from sender account called by transaction service
     * @param accountNumber
     * @param creditAmount
     */

    public void creditBalance(String accountNumber, BigDecimal creditAmount) {
        log.info("Credit account for  {}", accountNumber);
        Account account = accountRepository.findByAccountNumber(accountNumber).orElseThrow(()-> new RuntimeException("account not found"));
        account.getBalance().add(creditAmount);
        accountRepository.save(account);
        log.info("Credit account for  {}", account.getAccountNumber());
    }


}
