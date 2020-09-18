package com.antra.evaluation.reporting_system;

import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.text.ParseException;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(ParseException.class)
    public void handleUnProccessableServiceException() {
         
    }
}
