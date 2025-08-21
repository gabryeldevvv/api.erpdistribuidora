package com.api.erpdistribuidora.exception;

public class LocalNaoEncontradoException extends RuntimeException {
    public LocalNaoEncontradoException(Long id) {
        super("Local com ID " + id + " não encontrado");
    }
}
