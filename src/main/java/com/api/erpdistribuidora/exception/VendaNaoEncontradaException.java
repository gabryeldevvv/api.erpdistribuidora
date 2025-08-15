package com.api.erpdistribuidora.exception;

public class VendaNaoEncontradaException extends RuntimeException {
    public VendaNaoEncontradaException(Long id) {
        super("Venda com ID " + id + " não encontrada");
    }
}
