package com.starlingbank.tech.repository;

import com.starlingbank.tech.common.Tuple;
import com.starlingbank.tech.config.StarlingPropertyConfigurationHolder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.lang.invoke.MethodHandles;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@SuppressWarnings("Duplicates")
@Component
public class TransactionsRepository extends AbstractRepository {
    private static final Logger LOG = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

    @Autowired
    private StarlingPropertyConfigurationHolder starlingPropertyConfigurationHolder;

    @Autowired
    private RestTemplate restTemplate;

    public String getAllTransactions(Tuple<String, String> accountUidCategoryUidTuple, Tuple<LocalDateTime, LocalDateTime> dateRangeTuple) {
        try {
            Map<String, String> uriVariables = new HashMap<>(1);
            uriVariables.put("accountUid", accountUidCategoryUidTuple._1);
            uriVariables.put("categoryUid", accountUidCategoryUidTuple._2);

            String url = UriComponentsBuilder
                    .fromHttpUrl(starlingPropertyConfigurationHolder.getApiBaseUrl() + "/v2/feed/account/{accountUid}/category/{categoryUid}/transactions-between")
                    .queryParam("minTransactionTimestamp", dateRangeTuple._1)
                    .queryParam("maxTransactionTimestamp", dateRangeTuple._2)
                    .build().toUriString();

            HttpEntity<String> entity = new HttpEntity<>("body", getBasicAuthHttpHeaders(starlingPropertyConfigurationHolder.getClientAuthToken()));

            LOG.info("Invoking StarlingBank Transactions Feed API on {}", url.toString());

            ResponseEntity<String> exchange = restTemplate.exchange(url, HttpMethod.GET, entity, String.class);
            if (!exchange.hasBody()) {
                throw exceptionFunction.apply("No body received from transactions feed call").apply(null);
            }

            LOG.info("StarlingBank Transactions Feed API returned feed between dates on {}", url.toString());

            return exchange.getBody();
        } catch (Exception e) {
            LOG.error("Error whilst receiving response from /v2/feed", e.getCause());
            throw exceptionFunction.apply("Error whilst receiving response from /v2/feed").apply(e);
        }
    }
}

