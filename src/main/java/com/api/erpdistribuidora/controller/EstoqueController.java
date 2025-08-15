package com.api.erpdistribuidora.controller;

import com.api.erpdistribuidora.mapper.EstoqueMapper;
import com.api.erpdistribuidora.service.EstoqueService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/estoques")
@RequiredArgsConstructor
public class EstoqueController {

    private final EstoqueService estoqueService;
    private final EstoqueMapper estoqueMapper;

    @GetMapping("/produto/{idProduto}")
    public ResponseEntity<?> listarPorProduto(@PathVariable Long idProduto) {
        var lista = estoqueService.listarPorProduto(idProduto);
        return ResponseEntity.ok(estoqueMapper.toResponseDTOList(lista));
    }
}
