package com.starlingbank.tech.service;

import com.starlingbank.tech.common.Tuple;
import com.starlingbank.tech.config.StarlingPropertyConfigurationHolder;
import com.starlingbank.tech.domain.RoundupMoney;
import com.starlingbank.tech.exception.StarlingBusinessException;
import com.starlingbank.tech.repository.AccountRepository;
import com.starlingbank.tech.repository.TransactionsRepository;
import com.starlingbank.tech.util.DateTimeValidator;
import mjson.Json;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.lang.invoke.MethodHandles;
import java.time.*;
import java.time.format.DateTimeFormatter;
import java.util.*;

import static org.springframework.boot.Banner.Mode.LOG;

@Service
public class RoundUpAccountTransactions {
    private static final Logger LOG = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

    @Autowired
    private StarlingPropertyConfigurationHolder starlingPropertyConfigurationHolder;

    @Autowired
    private AccountRepository accountRepository;

    @Autowired
    private TransactionsRepository transactionsRepository;

    public Set<RoundupMoney> getAccountTransactionsRoundup(Tuple<String, String> accountUIDTransactionStartDate) {
        String customerAccountsJSON = accountRepository.getCustomerAccounts();
        Json accounts = Json.read(customerAccountsJSON).at("accounts");
        if (accounts == null || !accounts.isArray()) {
            throw new StarlingBusinessException("accounts information fetched from /v2/accounts API is not a recognizable json format");
        }

        List<Json> jsons = accounts.asJsonList();
        if (jsons.size() == 0) {
            //TODO: send 0 as roundup - since no transactions exist
        }

        Set<RoundupMoney> roundupAccountsSet = new HashSet<>(jsons.size());
        jsons.stream().forEach(account -> {
            String accountUid = account.at("accountUid").asString();
            Tuple<Instant, Instant> transactionsDateTimeBoundary = getTransactionDateRange(accountUIDTransactionStartDate._2);

            LOG.info("Account UID {}", accountUid);

            String feed = transactionsRepository.getAllTransactions(
                    Tuple.of(accountUid, account.at("defaultCategory").asString()),
                    transactionsDateTimeBoundary);

            RoundupMoney roundupMoney = new RoundupMoney();
            roundupMoney.setAccountUID(accountUid);
            roundupMoney.setFromDate(transactionsDateTimeBoundary._1);
            roundupMoney.setEndDate(transactionsDateTimeBoundary._2);
            roundupMoney.setRoundupAmount(null); //TODO: This is what we need to calculate

            System.out.println(feed);
            roundupAccountsSet.add(roundupMoney);
        });

        return roundupAccountsSet;
    }

    private Tuple<Instant, Instant> getTransactionDateRange(String startDate) {
        Optional<LocalDate> transactionStartLocalDate = DateTimeValidator.convertStringToLocalDate(DateTimeFormatter.ofPattern("yyyyMMdd", Locale.UK), startDate);
        if (!transactionStartLocalDate.isPresent()) {
            throw new StarlingBusinessException("Transaction Start Date does not comply with standard yyyyMMdd ISO8601 format - cannot convert to LocalDate!");
        }

        LocalDateTime transactionStartDateTime = transactionStartLocalDate.get().atStartOfDay();
        LocalDateTime transactionEndDateTime = transactionStartDateTime.plusWeeks(1).withHour(23).withMinute(59).withSecond(59);

        Optional<Instant> formattedZuluStartDateTime = DateTimeValidator.getUTCZuluDateTime(transactionStartDateTime);
        Optional<Instant> formattedZuluEndDateTime = DateTimeValidator.getUTCZuluDateTime(transactionEndDateTime);

        if(!formattedZuluStartDateTime.isPresent() || !formattedZuluEndDateTime.isPresent()) {
            throw new StarlingBusinessException("Cannot convert dates to Zulu / UTC format");
        }

        return Tuple.of(formattedZuluStartDateTime.get(), formattedZuluEndDateTime.get());
    }
}
