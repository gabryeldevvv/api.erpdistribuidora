package com.api.erpdistribuidora.controller;

import com.api.erpdistribuidora.dto.ItemVendaRequestDTO;
import com.api.erpdistribuidora.dto.ItemVendaResponseDTO;
import com.api.erpdistribuidora.service.ItemVendaService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/vendas/{idVenda}/itens")
@RequiredArgsConstructor
public class ItemVendaController {

    private final ItemVendaService itemVendaService;

    @PostMapping
    public ResponseEntity<ItemVendaResponseDTO> adicionarItem(@PathVariable Long idVenda,
                                                              @Valid @RequestBody ItemVendaRequestDTO dto) {
        return ResponseEntity.ok(itemVendaService.adicionarItem(idVenda, dto));
    }
}
