package com.api.erpdistribuidora.dto;

public record ImagemResponse(
        Integer id,
        String nome,
        String url,
        String path,
        Long produtoId
) {}
