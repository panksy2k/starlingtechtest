package com.starlingbank.tech.repository;

import com.starlingbank.tech.exception.StarlingBusinessException;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.web.client.RestClientException;

import java.util.Collections;
import java.util.function.Function;

abstract class AbstractRepository {
    protected final Function<String, Function<Exception, StarlingBusinessException>> exceptionFunction =
            msg -> error -> error == null? new StarlingBusinessException(msg) : new StarlingBusinessException(msg, error);


    protected HttpHeaders getBasicAuthHttpHeaders(String clientAuthToken) {
        HttpHeaders headers = new HttpHeaders();
        headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
        headers.setBearerAuth(clientAuthToken);

        return headers;
    }
}
