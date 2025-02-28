package com.example.demo.service;

import com.example.demo.entity.Account;
import com.example.demo.repository.AccountRepository;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AccountService {
    private final AccountRepository accountRepository;
    private static final Logger logger = LoggerFactory.getLogger(AccountService.class);

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void createAccount(Account account) {
        accountRepository.save(account);
    }

    public double getBalance(String accountNumber) {
        Account account = accountRepository.findByAccountNumber(accountNumber)
                .orElseThrow(() -> new RuntimeException("Account not found: " + accountNumber));
        return account.getBalance();
    }

    public Account findAccountByNumber(String accountNumber) {
        return accountRepository.findByAccountNumber(accountNumber)
                .orElseThrow(() -> new RuntimeException("Account not found: " + accountNumber));
    }

    public void updateAccountBalance(Account account, double balance) {
        account.setBalance(balance);
        accountRepository.save(account);
    }

    @Transactional
    public void debit(String accountNumber, double amount) {
        Account account = findAccountByNumber(accountNumber);

        if (account.getBalance() < amount) {
            throw new RuntimeException("Insufficient balance: " + accountNumber);
        }

        updateAccountBalance(account, account.getBalance() - amount);
        logger.info("debit: {}, account: {}", amount, account);
    }

    @Transactional
    public void credit(String accountNumber, double amount) {
        Account account = findAccountByNumber(accountNumber);
        updateAccountBalance(account, account.getBalance() + amount);
        logger.info("credit: {}, account: {}", amount, account);
    }

}
