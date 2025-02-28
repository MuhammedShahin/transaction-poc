package com.example.demo.service;

import com.example.demo.entity.Account;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Rollback;
import org.springframework.transaction.annotation.Transactional;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
@Transactional
@Rollback(false)
class TransactionServiceTest {
    @Autowired
    private TransactionService transactionService;
    @Autowired
    private AccountService accountService;
    private static final Logger logger = LoggerFactory.getLogger(TransactionServiceTest.class);

    @BeforeEach
    public void setup() {
        Account account1 = new Account();
        account1.setAccountNumber("ACC123");
        account1.setBalance(1000);
        accountService.createAccount(account1);

        Account account2 = new Account();
        account2.setAccountNumber("ACC456");
        account2.setBalance(500);
        accountService.createAccount(account2);
    }


    @Test
    void testConcurrentTransfers() throws InterruptedException {
        int numberOfThreads = 10;
        ExecutorService executorService = Executors.newFixedThreadPool(numberOfThreads);

        for (int i = 0; i < numberOfThreads; i++) {
            executorService.submit(() -> {
                try {
                    transactionService.logTransaction("ACC123", "ACC456", 100);
                    transactionService.transferMoneyReadUncommitted("ACC123", "ACC456", 100);
                } catch (Exception e) {
                    logger.error("Error during transfer: {}", e.getMessage());
                }
            });
        }

        executorService.shutdown();
        executorService.awaitTermination(10, TimeUnit.SECONDS);

        Account account1 = accountService.findAccountByNumber("ACC123");
        Account account2 = accountService.findAccountByNumber("ACC456");

        assertEquals(0, account1.getBalance(), "Account 1 balance should be 0");
        assertEquals(1500, account2.getBalance(), "Account 2 balance should be 1500");
    }
}

