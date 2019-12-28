package com.starlingbank.tech.domain;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.time.Instant;

@NoArgsConstructor
@Data
public class RoundupMoney implements Serializable {
    String accountUID;
    Money roundupAmount;
    Instant fromDate, endDate;
}
