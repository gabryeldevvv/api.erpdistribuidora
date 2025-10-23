// src/main/java/com/api/erpdistribuidora/exception/CategoriaJaExisteException.java
package com.api.erpdistribuidora.exception;

public class CategoriaJaExisteException extends RuntimeException {

    public CategoriaJaExisteException(String nome) {
        super("Já existe uma categoria com o nome: " + nome);
    }

    public CategoriaJaExisteException(Long id) {
        super("Já existe uma categoria com o ID: " + id);
    }

    public CategoriaJaExisteException(String mensagem, Throwable causa) {
        super(mensagem, causa);
    }
}
