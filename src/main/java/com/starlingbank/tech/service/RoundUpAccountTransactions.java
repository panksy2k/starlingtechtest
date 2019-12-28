package com.starlingbank.tech.service;

import com.starlingbank.tech.common.Tuple;
import com.starlingbank.tech.config.StarlingPropertyConfigurationHolder;
import com.starlingbank.tech.domain.Currency;
import com.starlingbank.tech.domain.Money;
import com.starlingbank.tech.domain.RoundupMoney;
import com.starlingbank.tech.exception.StarlingBusinessException;
import com.starlingbank.tech.repository.AccountRepository;
import com.starlingbank.tech.repository.TransactionsRepository;
import com.starlingbank.tech.transformer.TransactionFeedTransformer;
import com.starlingbank.tech.util.DateTimeUtil;
import mjson.Json;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.lang.invoke.MethodHandles;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.function.Function;

@Service
public class RoundUpAccountTransactions {
    private static final Logger LOG = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

    @Autowired
    private StarlingPropertyConfigurationHolder starlingPropertyConfigurationHolder;

    @Autowired
    private AccountRepository accountRepository;

    @Autowired
    private TransactionsRepository transactionsRepository;

    private final Function<BigDecimal, Double> bigToDouble = BigDecimal::doubleValue;
    private final Function<Double, Double> roundupCeiling = originalAmt -> Math.ceil(originalAmt) - originalAmt;
    private final Function<Double, BigDecimal> doubleToBig = dblAmt -> BigDecimal.valueOf(dblAmt).setScale(2, RoundingMode.HALF_UP);
    private final Function<BigDecimal, BigDecimal> roundedBigDecimalAmt = doubleToBig.compose(roundupCeiling.compose(bigToDouble));

    public Set<RoundupMoney> getAccountTransactionsRoundup(String transactionStartDate) {
        String customerAccountsJSON = accountRepository.getCustomerAccounts();
        Json accountsJsonUp = Json.read(customerAccountsJSON);

        if (!accountsJsonUp.has("accounts") || !accountsJsonUp.at("accounts").isArray()) {
            throw new StarlingBusinessException("Accounts information fetched from /v2/accounts API is not a recognizable json array format");
        }

        List<Json> jsons = accountsJsonUp.at("accounts").asJsonList();
        if (jsons.size() == 0) {
            throw new StarlingBusinessException("No accounts found for the customer -- and hence no transactions/roundups required!");
        }

        final Tuple<Instant, Instant> transactionsDateTimeBoundary = getTransactionDateRange(transactionStartDate);

        Set<RoundupMoney> roundupAccountsSet = new HashSet<>(jsons.size());
        for (Json account : jsons) {
            String accountUid = account.at("accountUid").asString();
            String feedTransactionsByAccount = transactionsRepository.getAllTransactions(
                    Tuple.of(accountUid, account.at("defaultCategory").asString()),
                    transactionsDateTimeBoundary);

            List<BigDecimal> allRoundedUpOutwardExpenses = TransactionFeedTransformer.transform(
                    feedTransactionsByAccount,
                    criteria -> criteria.has("direction") && criteria.at("direction").getValue().equals("OUT"),
                    roundedBigDecimalAmt);

            RoundupMoney roundupMoney = new RoundupMoney();
            roundupMoney.setAccountUID(accountUid);
            roundupMoney.setFromDate(transactionsDateTimeBoundary._1);
            roundupMoney.setEndDate(transactionsDateTimeBoundary._2);

            if (allRoundedUpOutwardExpenses.isEmpty()) {
                roundupMoney.setRoundupAmount(new Money(Currency.GBP, 0L));
            } else {
                BigDecimal totalRoundupAmount = allRoundedUpOutwardExpenses.stream().reduce(BigDecimal.ZERO, (a1, a2) -> a1.add(a2));
                roundupMoney.setRoundupAmount(new Money(Currency.GBP, totalRoundupAmount.multiply(BigDecimal.valueOf(100)).longValue()));
            }

            roundupAccountsSet.add(roundupMoney);
        }

        return roundupAccountsSet;
    }

    private Tuple<Instant, Instant> getTransactionDateRange(String startDate) {
        Optional<LocalDate> transactionStartLocalDate = DateTimeUtil.convertStringToLocalDate(DateTimeFormatter.ofPattern("yyyyMMdd", Locale.UK), startDate);
        if (!transactionStartLocalDate.isPresent()) {
            throw new StarlingBusinessException("Transaction Start Date does not comply with standard yyyyMMdd ISO8601 format - cannot convert to LocalDate!");
        }

        LocalDateTime transactionStartDateTime = transactionStartLocalDate.get().atStartOfDay();
        LocalDateTime transactionEndDateTime = transactionStartDateTime.plusWeeks(1).minusDays(1).withHour(23).withMinute(59).withSecond(59);

        Optional<Instant> formattedZuluStartDateTime = DateTimeUtil.getUTCZuluDateTime(transactionStartDateTime);
        Optional<Instant> formattedZuluEndDateTime = DateTimeUtil.getUTCZuluDateTime(transactionEndDateTime);

        if (!formattedZuluStartDateTime.isPresent() || !formattedZuluEndDateTime.isPresent()) {
            throw new StarlingBusinessException("Cannot convert dates to Zulu / UTC format");
        }

        return Tuple.of(formattedZuluStartDateTime.get(), formattedZuluEndDateTime.get());
    }
}
