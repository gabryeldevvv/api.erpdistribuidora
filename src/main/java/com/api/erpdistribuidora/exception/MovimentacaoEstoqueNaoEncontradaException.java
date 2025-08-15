package com.api.erpdistribuidora.exception;

public class MovimentacaoEstoqueNaoEncontradaException extends RuntimeException {
    public MovimentacaoEstoqueNaoEncontradaException(Long id) {
        super("Movimentação de estoque com ID " + id + " não encontrada");
    }
}

