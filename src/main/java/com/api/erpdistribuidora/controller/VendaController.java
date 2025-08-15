package com.api.erpdistribuidora.controller;

import com.api.erpdistribuidora.dto.StatusUpdateRequestDTO;
import com.api.erpdistribuidora.dto.VendaRequestDTO;
import com.api.erpdistribuidora.dto.VendaResponseDTO;
import com.api.erpdistribuidora.service.VendaService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import jakarta.validation.Valid;

import java.util.List;

@RestController
@RequestMapping("/vendas")
@RequiredArgsConstructor
public class VendaController {

    private final VendaService vendaService;

    @GetMapping
    public ResponseEntity<List<VendaResponseDTO>> listarTodas() {
        return ResponseEntity.ok(vendaService.listarTodas());
    }

    @GetMapping("/{id}")
    public ResponseEntity<VendaResponseDTO> buscarPorId(@PathVariable Long id) {
        return ResponseEntity.ok(vendaService.buscarPorId(id));
    }

    @PostMapping
    public ResponseEntity<VendaResponseDTO> criar(@Valid @RequestBody VendaRequestDTO dto) {
        return ResponseEntity.ok(vendaService.criar(dto));
    }

    @PutMapping("/{id}/status")
    public ResponseEntity<VendaResponseDTO> atualizarStatus(@PathVariable Long id,
                                                            @Valid @RequestBody StatusUpdateRequestDTO dto) {
        return ResponseEntity.ok(vendaService.atualizarStatus(id, dto.getStatus()));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletar(@PathVariable Long id) {
        vendaService.deletar(id);
        return ResponseEntity.noContent().build();
    }
}
