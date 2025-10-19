package com.api.erpdistribuidora.controller;

import com.api.erpdistribuidora.dto.EstoqueRequestDTO;
import com.api.erpdistribuidora.dto.EstoqueResponseDTO;
import com.api.erpdistribuidora.service.EstoqueService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/estoques")
@RequiredArgsConstructor
public class EstoqueController {

    private final EstoqueService estoqueService;
    private final com.api.erpdistribuidora.mapper.EstoqueMapper estoqueMapper;

    @GetMapping
    public ResponseEntity<List<EstoqueResponseDTO>> listar() {
        return ResponseEntity.ok(estoqueService.listar());
    }

    @GetMapping("/{id}")
    public ResponseEntity<EstoqueResponseDTO> buscarPorId(@PathVariable Long id) {
        return ResponseEntity.ok(estoqueService.buscarPorId(id));
    }

    @PostMapping
    public ResponseEntity<EstoqueResponseDTO> criar(@Valid @RequestBody EstoqueRequestDTO dto) {
        var criado = estoqueService.criar(dto);
        return ResponseEntity.created(URI.create("/estoques/" + criado.getId())).body(criado);
    }

    @PutMapping("/{id}")
    public ResponseEntity<EstoqueResponseDTO> atualizar(@PathVariable Long id,
            @Valid @RequestBody EstoqueRequestDTO dto) {
        var atualizado = estoqueService.atualizar(id, estoqueMapper.toEntity(dto));
        return ResponseEntity.ok(estoqueMapper.toResponseDTO(atualizado));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> remover(@PathVariable Long id) {
        estoqueService.remover(id);
        return ResponseEntity.noContent().build();
    }
}
