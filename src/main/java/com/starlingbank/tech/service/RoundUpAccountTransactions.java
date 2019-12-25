package com.starlingbank.tech.service;

import com.starlingbank.tech.common.Tuple;
import com.starlingbank.tech.config.StarlingPropertyConfigurationHolder;
import com.starlingbank.tech.exception.StarlingBusinessException;
import com.starlingbank.tech.repository.AccountRepository;
import com.starlingbank.tech.repository.TransactionsRepository;
import com.starlingbank.tech.util.DateTimeValidator;
import mjson.Json;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Locale;
import java.util.Optional;

@Service
public class RoundUpAccountTransactions {
    @Autowired
    private StarlingPropertyConfigurationHolder starlingPropertyConfigurationHolder;

    @Autowired
    private AccountRepository accountRepository;

    @Autowired
    private TransactionsRepository transactionsRepository;

    public String getAccountTransactionsRoundup(Tuple<String, String> accountUIDTransactionStartDate) {
        String customerAccountsJSON = accountRepository.getCustomerAccounts();
        Json accounts = Json.read(customerAccountsJSON).at("accounts");
        if(accounts == null || !accounts.isArray()) {
            throw new StarlingBusinessException("accounts information fetched from /v2/accounts API is not a recognizable json format");
        }

        List<Json> jsons = accounts.asJsonList();
        if(jsons.size() == 0) {
            //TODO: send 0 as roundup - since no transactions exist
        }

        for(Json account : jsons) {
            String feed = transactionsRepository.getAllTransactions(
                    Tuple.of(account.at("accountUid").asString(), account.at("defaultCategory").asString()),
                    getTransactionDateRange(accountUIDTransactionStartDate._2));
        }

        //accountUIDTransactionStartDate._2;
        return accounts.toString();
    }

    private Tuple<LocalDateTime, LocalDateTime> getTransactionDateRange(String startDate) {
        Optional<LocalDate> transactionStartLocalDate = DateTimeValidator.convertStringToLocalDate(DateTimeFormatter.ofPattern("yyyyMMdd", Locale.UK), startDate);
        if(!transactionStartLocalDate.isPresent()) {
            throw new StarlingBusinessException("Transaction Start Date does not comply with yyyyMMdd ISO format - cannot convert to LocalDate!");
        }

        LocalDate localDateStart = transactionStartLocalDate.get();
        LocalDateTime transactionStartDateTime = localDateStart.atStartOfDay();
        LocalDateTime transactionEndDateTime = localDateStart.plusWeeks(1).atTime(23, 59, 59);

        return Tuple.of(transactionStartDateTime, transactionEndDateTime);
    }
}
