package com.starlingbank.tech.util;

import java.time.*;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.Locale;
import java.util.Optional;

public class DateTimeUtil {
    public static boolean isDateISOFormat(String format, String value, Locale locale) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern(format, locale);

        try {
            Optional<LocalDate> localDateOptional = convertStringToLocalDate(formatter, value);
            return !localDateOptional.isPresent()? false : localDateOptional.get().format(formatter).equals(value);
        } catch (DateTimeParseException e) {
            return false;
        }
    }

    public static Optional<LocalDate> convertStringToLocalDate(DateTimeFormatter dateTimeFormatter, String value) {
        try {
            return Optional.of(LocalDate.parse(value, dateTimeFormatter));
        } catch (DateTimeParseException e) {
            return Optional.empty();
        }
    }

    public static Optional<Instant> getUTCZuluDateTime(LocalDateTime aDateTime) {
        ZonedDateTime utc = ZonedDateTime.of(LocalDate.of(aDateTime.getYear(), aDateTime.getMonth(), aDateTime.getDayOfMonth()),
                LocalTime.of(aDateTime.getHour(), aDateTime.getMinute(), aDateTime.getSecond()),
                ZoneId.of("UTC"));

        try {
            return Optional.of(utc.toInstant());
        } catch(DateTimeException e) {
            return Optional.empty();
        }
    }
}
