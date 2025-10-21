package com.api.erpdistribuidora.exception;

public class UsuarioNaoEncontradoException extends RuntimeException {
    public UsuarioNaoEncontradoException(Long id) {
        super("Usuário não encontrado: " + id);
    }
}
