package com.bellagestora.auth_service.config;

import org.springframework.amqp.AmqpException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {
    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<String> handleException(IllegalArgumentException e) {
        return ResponseEntity.status(HttpStatus.CONFLICT).body(e.getMessage());
    }
    @ExceptionHandler(AmqpException.class)
    public ResponseEntity<String> handleAmqException(AmqpException e) {
        return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE).body(
                "Serviço temporariamente indisponível. Por favor, tente novamente mais tarde."
        );
    }
}
