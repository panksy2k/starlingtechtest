package com.starlingbank.tech.domain;

import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.experimental.FieldDefaults;
import java.io.Serializable;

@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Data
public class Money implements Serializable {
    private static final long serialVersionUID = 831060897247L;

    Currency currency;
    long minorAmountUnit;
}
