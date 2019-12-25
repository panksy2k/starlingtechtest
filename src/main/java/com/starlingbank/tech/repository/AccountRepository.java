package com.starlingbank.tech.repository;

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
import java.util.HashMap;
import java.util.Map;

@Component
public class AccountRepository extends AbstractRepository {
    private static final Logger LOG = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

    @Autowired
    private StarlingPropertyConfigurationHolder starlingPropertyConfigurationHolder;

    @Autowired
    private RestTemplate restTemplate;

    public String getCustomerAccounts() {
        try {
            Map<String, String> uriVariables = new HashMap<>(1);
            uriVariables.put("path", "v2/accounts");

            String url = UriComponentsBuilder
                    .fromHttpUrl(starlingPropertyConfigurationHolder.getApiBaseUrl() + "/v2/accounts").build().toUriString();

            HttpEntity<String> entity = new HttpEntity<>("body", getBasicAuthHttpHeaders(starlingPropertyConfigurationHolder.getClientAuthToken()));

            LOG.info("Invoking StarlingBank Accounts API on {}", url.toString());

            ResponseEntity<String> exchange = restTemplate.exchange(url, HttpMethod.GET, entity, String.class);
            if(!exchange.hasBody()) {
                throw exceptionFunction.apply("No body received from /v2/accounts call").apply(null);
            }

            LOG.info("StarlingBank Accounts API provided accounts information on {}", url.toString());

            return exchange.getBody();
        } catch(Exception e) {
            LOG.error("Error whilst receiving response from /accounts", e.getCause());
            throw exceptionFunction.apply("Error whilst receiving response from /accounts").apply(e);
        }
    }
}
