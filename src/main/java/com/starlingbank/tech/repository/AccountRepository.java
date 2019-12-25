package com.starlingbank.tech.repository;

import com.starlingbank.tech.config.StarlingPropertyConfigurationHolder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.lang.invoke.MethodHandles;
import java.net.URI;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

@Component
public class AccountRepository extends AbstractRepository {
    private static final Logger LOG = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

    private final Function<String, Function<Exception, RestClientException>> clientExceptionFunction =
            msg -> error -> new RestClientException("Error whilst receiving response from /accounts", error);

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


            HttpHeaders headers = new HttpHeaders();
            headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
            headers.setBearerAuth(starlingPropertyConfigurationHolder.getClientAuthToken());
            HttpEntity<String> entity = new HttpEntity<>("body", headers);

            LOG.info("Invoking API {}", url.toString());

            ResponseEntity<String> exchange = restTemplate.exchange(url, HttpMethod.GET, entity, String.class);
            if(!exchange.hasBody()) {
                throw clientExceptionFunction.apply("No body received from /v2/accounts call").apply(null);
            }

            return exchange.getBody();
        }
        catch(Exception e) {
            LOG.error("Error whilst receiving response from /accounts", e.getCause());
            throw clientExceptionFunction.apply("Error whilst receiving response from /accounts").apply(e);
        }
    }
}
