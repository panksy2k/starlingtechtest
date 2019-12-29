package com.starlingbank.tech;

import com.starlingbank.tech.domain.Currency;
import com.starlingbank.tech.domain.Money;
import com.starlingbank.tech.domain.RoundupMoney;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class StaticDataHelper {

    public static final String accountUID = "adasdfgu-sdfhdsiufg22ddsf-sadgdhf";
    public static final Money roundupMoney = new Money(Currency.GBP, 189);
    public static final Instant startDate = Instant.now();
    public static final Instant endDate = startDate.plus(6, ChronoUnit.DAYS);

    public static Set<RoundupMoney> getAccountsRoundupDetails() {
        RoundupMoney roundupMoneyDetails = new RoundupMoney();
        roundupMoneyDetails.setAccountUID(accountUID);
        roundupMoneyDetails.setRoundupAmount(roundupMoney);
        roundupMoneyDetails.setFromDate(startDate);
        roundupMoneyDetails.setEndDate(endDate);

        return Stream.of(roundupMoneyDetails).collect(Collectors.toSet());
    }
}
