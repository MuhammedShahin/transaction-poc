package com.example.demo.service;

import com.example.demo.entity.Account;
import com.example.demo.repository.AccountRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AccountService {
    private final AccountRepository accountRepository;

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
    }

    @Transactional
    public void credit(String accountNumber, double amount) {
        Account account = findAccountByNumber(accountNumber);
        updateAccountBalance(account, account.getBalance() + amount);
    }

}
