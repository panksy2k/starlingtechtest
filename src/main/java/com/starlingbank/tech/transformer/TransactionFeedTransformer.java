package com.starlingbank.tech.transformer;

import com.starlingbank.tech.common.Tuple;
import com.starlingbank.tech.domain.Currency;
import com.starlingbank.tech.domain.RoundupMoney;
import mjson.Json;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.invoke.MethodHandles;
import java.math.BigDecimal;
import java.util.Collections;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;

public class TransactionFeedTransformer {
    private static final Logger LOG = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());
    private static final Consumer<? super Object> peekLogger = somethingToLog -> LOG.info(somethingToLog.toString());

    public static List<Tuple<Currency, BigDecimal>> transform(String feedItems,
                                                              Predicate<Json> filterTransactionCondition,
                                                              Function<BigDecimal, BigDecimal> roundupCriteria) {
        Json readFeedItemsParentJson = Json.read(feedItems);
        if(!readFeedItemsParentJson.has("feedItems") || !readFeedItemsParentJson.at("feedItems").isArray()) {
            return Collections.emptyList();
        }

        return readFeedItemsParentJson.at("feedItems").asJsonList().stream()
                .filter(filterTransactionCondition)
                .peek(j -> peekLogger.accept(j))
                .map(jsonTransaction -> jsonTransaction.at("amount", Json.read("{\"amount\":{\"currency\":\"GBP\",\"minorUnits\":0}}")))
                .map(jsonAmount -> Tuple.of(Currency.getCurrencyByValue(jsonAmount.at("currency").asString()), jsonAmount.at("minorUnits").asLong()))
                .map(minorAmountLong -> Tuple.of(minorAmountLong._1, BigDecimal.valueOf(minorAmountLong._2).divide(BigDecimal.valueOf(100))))
                .map(actualAmt -> Tuple.of(actualAmt._1, roundupCriteria.apply(actualAmt._2)))
                .peek(j -> peekLogger.accept(j))
                .collect(Collectors.toList());
    }
}
