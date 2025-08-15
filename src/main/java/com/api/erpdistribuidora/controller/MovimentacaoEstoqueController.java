package com.api.erpdistribuidora.controller;

import com.api.erpdistribuidora.mapper.MovimentacaoEstoqueMapper;
import com.api.erpdistribuidora.service.MovimentacaoEstoqueService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/movimentacoes-estoque")
@RequiredArgsConstructor
public class MovimentacaoEstoqueController {

    private final MovimentacaoEstoqueService movimentacaoService;
    private final MovimentacaoEstoqueMapper mapper;

    @GetMapping("/produto/{idProduto}")
    public ResponseEntity<?> listarPorProduto(@PathVariable Long idProduto) {
        var lista = movimentacaoService.listarPorProduto(idProduto);
        return ResponseEntity.ok(mapper.toResponseDTOList(lista));
    }
}


