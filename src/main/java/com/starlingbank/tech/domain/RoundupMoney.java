package com.starlingbank.tech.domain;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;
import java.time.LocalDate;

@NoArgsConstructor
@Setter @Getter
public class RoundupMoney implements Serializable {
    String accountUID;
    Money roundupAmount;
    LocalDate fromDate, endDate;
}
