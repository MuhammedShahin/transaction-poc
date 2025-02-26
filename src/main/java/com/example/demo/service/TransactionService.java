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
            isolation = Isolation.READ_COMMITTED,
            propagation = Propagation.REQUIRED
    )
    public void transferMoney(String fromAccountNumber, String toAccountNumber, double amount) {
        accountService.debit(fromAccountNumber, amount);

        accountService.credit(toAccountNumber, amount);
    }

    @Transactional(
            isolation = Isolation.SERIALIZABLE
    )
    public void transferMoneySerializable(String fromAccountNumber, String toAccountNumber, double amount) {
        accountService.debit(fromAccountNumber, amount);

        accountService.credit(toAccountNumber, amount);
    }

    @Transactional(
            propagation = Propagation.REQUIRES_NEW
    )
    public void logTransaction(String fromAccountNumber, String toAccountNumber, double amount) {
        logger.info("Transaction logged: {} -> {} Amount: {}", fromAccountNumber, toAccountNumber, amount);
    }
}
