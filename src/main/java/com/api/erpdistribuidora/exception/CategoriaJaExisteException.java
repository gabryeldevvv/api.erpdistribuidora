package com.api.erpdistribuidora.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.CONFLICT )
public class CategoriaJaExisteException extends RuntimeException {
    public CategoriaJaExisteException(String message) {
        super(message);
    }
}
