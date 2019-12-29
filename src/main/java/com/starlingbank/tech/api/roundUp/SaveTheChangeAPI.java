package com.starlingbank.tech.api.roundUp;

import com.starlingbank.tech.api.AbstractRoundupController;
import com.starlingbank.tech.domain.RoundupMoney;
import com.starlingbank.tech.service.RoundUpAccountTransactions;
import com.starlingbank.tech.util.DateTimeUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.ConstraintViolationException;
import java.lang.invoke.MethodHandles;
import java.util.Locale;
import java.util.Set;

@RestController
@RequestMapping("/roundup")
public class SaveTheChangeAPI extends AbstractRoundupController {
    private static final Logger LOG = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

    @Autowired
    private RoundUpAccountTransactions roundUpAccountTransactions;

    @GetMapping(value = "/account/transactions/startDate/{startDate}")
    public ResponseEntity<Set<RoundupMoney>> doRoundUpOnWeeklyTransactions(@PathVariable("startDate") int isoStartDate) {
        LOG.info("Calling API to round-up the transactions");

        //Check if startDate is correct format or not
        boolean isDateISOFormat = DateTimeUtil.isDateISOFormat("yyyyMMdd", String.valueOf(isoStartDate), Locale.UK);
        if (!isDateISOFormat) {
            throw new ConstraintViolationException("startDate should be in yyyyMMdd format!", null);
        }

        //Get the transactions for an account under a week and roundup the spent amount
        return ResponseEntity.ok(roundUpAccountTransactions.getAccountTransactionsRoundup(String.valueOf(isoStartDate)));
    }
}