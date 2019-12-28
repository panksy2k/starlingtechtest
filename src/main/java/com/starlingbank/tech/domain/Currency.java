package com.starlingbank.tech.domain;

public enum Currency {
    GBP,
    EUR,
    USD,
    BRL,
    UNKNOWN;

    public static Currency getCurrencyByValue(String currencyStringValue) {
        if(currencyStringValue == null || currencyStringValue.isEmpty()) {
            return UNKNOWN;
        }

        for(Currency c : Currency.values()) {
            if(c.name().equals(currencyStringValue.toUpperCase())) {
                return c;
            }
        }

        return UNKNOWN;
    }
}
