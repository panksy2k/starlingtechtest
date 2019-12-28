package com.starlingbank.tech.transformer;

import mjson.Json;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Collections;
import java.util.List;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;

public class TransactionFeedTransformer {
    public static List<BigDecimal> transform(String feedItems,
                                                   Predicate<Json> filterTransactionCondition,
                                                   Function<BigDecimal, BigDecimal> roundupCriteria) {
        Json readFeedItemsParentJson = Json.read(feedItems);
        if(!readFeedItemsParentJson.has("feedItems") || !readFeedItemsParentJson.at("feedItems").isArray()) {
            return Collections.emptyList();
        }

        return readFeedItemsParentJson.at("feedItems").asJsonList().stream()
                .filter(filterTransactionCondition)
                .map(jsonTransaction -> jsonTransaction.at("amount", Json.read("{\"amount\":{\"currency\":\"GBP\",\"minorUnits\":0}}")))
                .map(jsonAmount -> jsonAmount.at("minorUnits").asLong())
                .map(minorAmountLong -> BigDecimal.valueOf(minorAmountLong).divide(BigDecimal.valueOf(100), RoundingMode.UP))
                .map(roundupCriteria)
                .collect(Collectors.toList());
    }
}
