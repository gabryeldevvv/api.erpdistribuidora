package com.api.erpdistribuidora.exception;

public class EstoqueInsuficienteException extends RuntimeException {
    public EstoqueInsuficienteException(Long idProduto, int disponivel, int solicitado) {
        super("Estoque insuficiente para o produto ID " + idProduto +
                " (disponível: " + disponivel + ", necessário: " + solicitado + ")");
    }
}
