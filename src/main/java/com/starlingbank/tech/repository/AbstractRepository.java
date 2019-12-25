package com.starlingbank.tech.repository;

import com.starlingbank.tech.common.Tuple;
import org.springframework.http.HttpHeaders;

import java.util.List;
import java.util.function.Function;

abstract class AbstractRepository {
    /*protected Function<List<Tuple<String, String>>, HttpHeaders> createHttpHeaders = headersTuple -> {
        HttpHeaders headers = new HttpHeaders();
        for (Tuple<String, String> header : headersTuple) {
            headers.add(header._1, header._2);
        }

        return headers;
    };*/
}
