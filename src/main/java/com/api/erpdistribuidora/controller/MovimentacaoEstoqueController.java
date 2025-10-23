// src/main/java/com/api/erpdistribuidora/controller/MovimentacaoEstoqueController.java
package com.api.erpdistribuidora.controller;

import com.api.erpdistribuidora.dto.MovimentacaoEstoqueRequestDTO;
import com.api.erpdistribuidora.dto.MovimentacaoEstoqueResponseDTO;
import com.api.erpdistribuidora.service.MovimentacaoEstoqueService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/movimentacoes-estoque")
@RequiredArgsConstructor
public class MovimentacaoEstoqueController {

    private final MovimentacaoEstoqueService movimentacaoService;

    @GetMapping
    public ResponseEntity<List<MovimentacaoEstoqueResponseDTO>> listarTodas() {
        return ResponseEntity.ok(movimentacaoService.listarTodas());
    }

    @GetMapping("/produto/{idProduto}")
    public ResponseEntity<List<MovimentacaoEstoqueResponseDTO>> listarPorProduto(@PathVariable Long idProduto) {
        return ResponseEntity.ok(movimentacaoService.listarPorProduto(idProduto));
    }

    @PostMapping
    public ResponseEntity<MovimentacaoEstoqueResponseDTO> criar(@Valid @RequestBody MovimentacaoEstoqueRequestDTO dto) {
        return ResponseEntity.ok(movimentacaoService.criar(dto));
    }
}
