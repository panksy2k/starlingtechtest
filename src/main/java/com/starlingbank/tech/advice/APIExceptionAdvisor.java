package com.starlingbank.tech.advice;

import com.starlingbank.tech.domain.ErrorResponse;
import com.starlingbank.tech.exception.StarlingBusinessException;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import javax.validation.ConstraintViolationException;

@ControllerAdvice
public class APIExceptionAdvisor extends ResponseEntityExceptionHandler {

    @ExceptionHandler(value = { IllegalArgumentException.class, IllegalStateException.class, ConstraintViolationException.class })
    protected ResponseEntity<Object> handleConflictConstraints(RuntimeException ex, WebRequest request) {
        return new ResponseEntity(new ErrorResponse(1, ex.getMessage()), new HttpHeaders(), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(value = { StarlingBusinessException.class })
    protected ResponseEntity<Object> handleBusinessOrTechnicalError(StarlingBusinessException ex, WebRequest request) {
        return new ResponseEntity(new ErrorResponse(500, ex.getMessage()), new HttpHeaders(), HttpStatus.INTERNAL_SERVER_ERROR);
    }
}
