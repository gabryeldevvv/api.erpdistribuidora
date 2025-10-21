package com.api.erpdistribuidora.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.CONFLICT )
public class CategoriaComFilhosException extends RuntimeException {
    public CategoriaComFilhosException(String message) {
        super(message);
    }
}
