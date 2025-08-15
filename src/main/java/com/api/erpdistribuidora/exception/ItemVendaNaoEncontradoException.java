package com.api.erpdistribuidora.exception;

public class ItemVendaNaoEncontradoException extends RuntimeException {
    public ItemVendaNaoEncontradoException(Long id) {
        super("Item de venda com ID " + id + " não encontrado");
    }
}
