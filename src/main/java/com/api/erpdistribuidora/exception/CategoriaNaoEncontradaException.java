// src/main/java/com/api/erpdistribuidora/exception/CategoriaNaoEncontradaException.java
package com.api.erpdistribuidora.exception;

public class CategoriaNaoEncontradaException extends RuntimeException {
    public CategoriaNaoEncontradaException(Long id) {
        super("Categoria com ID " + id + " não encontrada");
    }
    public CategoriaNaoEncontradaException(String idPublico) {
        super("Categoria com idPublico '" + idPublico + "' não encontrada");
    }
}
