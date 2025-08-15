package com.api.erpdistribuidora.exception;

public class EstoqueNaoEncontradoException extends RuntimeException {
    public EstoqueNaoEncontradoException(Long id) {
        super("Estoque com ID " + id + " não encontrado");
    }
}
