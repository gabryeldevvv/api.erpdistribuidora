// src/main/java/com/api/erpdistribuidora/controller/EstoqueController.java
package com.api.erpdistribuidora.controller;

import com.api.erpdistribuidora.dto.EstoqueRequestDTO;
import com.api.erpdistribuidora.dto.EstoqueResponseDTO;
import com.api.erpdistribuidora.mapper.EstoqueMapper;
import com.api.erpdistribuidora.model.Estoque;
import com.api.erpdistribuidora.service.EstoqueService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/estoques")
@RequiredArgsConstructor
public class EstoqueController {

    private final EstoqueService estoqueService;
    private final EstoqueMapper estoqueMapper;

    // GET /estoques → lista geral
    @GetMapping
    public ResponseEntity<List<EstoqueResponseDTO>> listarTodos() {
        var lista = estoqueService.listarTodos();
        return ResponseEntity.ok(estoqueMapper.toResponseDTOList(lista));
    }

    // GET /estoques/{id} → detalhe
    @GetMapping("/{id}")
    public ResponseEntity<EstoqueResponseDTO> obter(@PathVariable Long id) {
        var entity = estoqueService.buscarPorId(id);
        return ResponseEntity.ok(estoqueMapper.toResponseDTO(entity));
    }

    // GET /estoques/produto/{idProduto} → lista por produto
    @GetMapping("/produto/{idProduto}")
    public ResponseEntity<List<EstoqueResponseDTO>> listarPorProduto(@PathVariable Long idProduto) {
        var lista = estoqueService.listarPorProduto(idProduto);
        return ResponseEntity.ok(estoqueMapper.toResponseDTOList(lista));
    }

    // POST /estoques → cria top-level (tela /estoques)
    @PostMapping
    public ResponseEntity<EstoqueResponseDTO> criar(@Valid @RequestBody EstoqueRequestDTO dto) {
        Estoque entity = estoqueMapper.toEntity(dto);
        var salvo = estoqueService.criar(entity);
        var body = estoqueMapper.toResponseDTO(salvo);
        return ResponseEntity
                .created(URI.create("/estoques/" + body.getId()))
                .body(body);
    }

    // POST /estoques/produto/{idProduto} → cria já atrelado ao produto da rota
    @PostMapping("/produto/{idProduto}")
    public ResponseEntity<EstoqueResponseDTO> criarParaProduto(
            @PathVariable Long idProduto,
            @Valid @RequestBody EstoqueRequestDTO dto
    ) {
        dto.setIdProduto(idProduto); // garante consistência com a rota
        var salvo = estoqueService.criar(estoqueMapper.toEntity(dto));
        var body = estoqueMapper.toResponseDTO(salvo);
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .location(URI.create("/estoques/" + body.getId()))
                .body(body);
    }

    // PUT /estoques/{id} → atualização
    @PutMapping("/{id}")
    public ResponseEntity<EstoqueResponseDTO> atualizar(
            @PathVariable Long id,
            @Valid @RequestBody EstoqueRequestDTO dto
    ) {
        Estoque atualizacoes = estoqueMapper.toEntity(dto);
        var atualizado = estoqueService.atualizar(id, atualizacoes);
        return ResponseEntity.ok(estoqueMapper.toResponseDTO(atualizado));
    }

    // DELETE /estoques/{id} → exclusão
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> remover(@PathVariable Long id) {
        estoqueService.remover(id);
        return ResponseEntity.noContent().build();
    }
}
