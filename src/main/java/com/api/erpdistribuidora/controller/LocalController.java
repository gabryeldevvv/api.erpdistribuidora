package com.api.erpdistribuidora.controller;

import com.api.erpdistribuidora.dto.LocalRequestDTO;
import com.api.erpdistribuidora.dto.LocalResponseDTO;
import com.api.erpdistribuidora.service.LocalService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import jakarta.validation.Valid;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/locais")
@RequiredArgsConstructor
public class LocalController {

    private final LocalService localService;

    @GetMapping
    public ResponseEntity<List<LocalResponseDTO>> listar() {
        return ResponseEntity.ok(localService.listar());
    }

    @GetMapping("/{id}")
    public ResponseEntity<LocalResponseDTO> buscarPorId(@PathVariable Long id) {
        return ResponseEntity.ok(localService.buscarPorId(id));
    }

    @PostMapping
    public ResponseEntity<LocalResponseDTO> criar(@Valid @RequestBody LocalRequestDTO dto) {
        LocalResponseDTO criado = localService.criar(dto);
        return ResponseEntity.created(URI.create("/locais/" + criado.getId())).body(criado);
    }

    @PutMapping("/{id}")
    public ResponseEntity<LocalResponseDTO> atualizar(@PathVariable Long id,
                                                      @Valid @RequestBody LocalRequestDTO dto) {
        return ResponseEntity.ok(localService.atualizar(id, dto));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletar(@PathVariable Long id) {
        localService.remover(id);
        return ResponseEntity.noContent().build();
    }
}
