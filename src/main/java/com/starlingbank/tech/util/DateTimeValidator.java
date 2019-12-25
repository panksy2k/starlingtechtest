package com.starlingbank.tech.util;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.Locale;

public class DateTimeValidator {
    public static boolean isDateISOFormat(String format, String value, Locale locale) {
        LocalDate localDate = null;
        DateTimeFormatter fomatter = DateTimeFormatter.ofPattern(format, locale);

        try {
            localDate = LocalDate.parse(value, fomatter);
            String result = localDate.format(fomatter);
            return result.equals(value);
        } catch (DateTimeParseException e) {
            return false;
        }
    }
}
