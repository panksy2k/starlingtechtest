package com.starlingbank.tech.service;

import com.starlingbank.tech.common.Tuple;
import com.starlingbank.tech.config.StarlingPropertyConfigurationHolder;
import com.starlingbank.tech.repository.AccountRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class RoundUpAccountTransactions {
    @Autowired
    private StarlingPropertyConfigurationHolder starlingPropertyConfigurationHolder;

    @Autowired
    private AccountRepository accountRepository;

    public String getAccountTransactionsRoundup(Tuple<String, String> accountUIDTransactionStartDate) {
        return accountRepository.getCustomerAccounts();
    }
}
