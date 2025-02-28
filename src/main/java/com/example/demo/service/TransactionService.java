package com.example.demo.service;

import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class TransactionService {
    private final AccountService accountService;
    private static final Logger logger = LoggerFactory.getLogger(TransactionService.class);

    @Transactional(
            isolation = Isolation.READ_COMMITTED
    )
    public void transferMoney(String fromAccountNumber, String toAccountNumber, double amount) {
        accountService.debit(fromAccountNumber, amount);
        simulateDelay();
        accountService.credit(toAccountNumber, amount);
    }

    @Transactional(
            isolation = Isolation.SERIALIZABLE
    )
    public void transferMoneySerializable(String fromAccountNumber, String toAccountNumber, double amount) {
        accountService.debit(fromAccountNumber, amount);
        simulateDelay();
        accountService.credit(toAccountNumber, amount);
    }

    @Transactional(
            isolation = Isolation.READ_UNCOMMITTED
    )
    public void transferMoneyReadUncommitted(String fromAccountNumber, String toAccountNumber, double amount) {
        accountService.debit(fromAccountNumber, amount);
        simulateDelay();
        accountService.credit(toAccountNumber, amount);
    }

    @Transactional(
            isolation = Isolation.REPEATABLE_READ
    )
    public void transferMoneyRepeatableRead(String fromAccountNumber, String toAccountNumber, double amount) {
        accountService.debit(fromAccountNumber, amount);
        simulateDelay();
        accountService.credit(toAccountNumber, amount);
    }

    @Transactional(
            propagation = Propagation.REQUIRES_NEW
    )
    public void logTransaction(String fromAccountNumber, String toAccountNumber, double amount) {
        logger.info("Transaction logged: {} -> {} Amount: {}", fromAccountNumber, toAccountNumber, amount);
    }

    private void simulateDelay() {
        try {
            Thread.sleep(500);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            logger.error("Thread interrupted during delay simulation", e);
        }
    }
}
