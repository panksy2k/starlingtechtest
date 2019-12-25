package com.starlingbank.tech.service;

import com.starlingbank.tech.common.Tuple;
import com.starlingbank.tech.config.StarlingPropertyConfigurationHolder;
import com.starlingbank.tech.exception.StarlingBusinessException;
import com.starlingbank.tech.repository.AccountRepository;
import mjson.Json;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class RoundUpAccountTransactions {
    @Autowired
    private StarlingPropertyConfigurationHolder starlingPropertyConfigurationHolder;

    @Autowired
    private AccountRepository accountRepository;

    public String getAccountTransactionsRoundup(Tuple<String, String> accountUIDTransactionStartDate) {
        String customerAccountsJSON = accountRepository.getCustomerAccounts();
        Json accounts = Json.read(customerAccountsJSON).at("accounts");
        if(accounts == null || !accounts.isArray()) {
            throw new StarlingBusinessException("accounts information fetched from /v2/accounts API is not a recognizable json format");
        }

        List<Json> jsons = accounts.asJsonList();
        for(Json account : jsons) {
            System.out.println(account.toString());
        }

        //accountUIDTransactionStartDate._2;
        return accounts.toString();
    }
}
