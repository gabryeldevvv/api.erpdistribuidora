package com.api.erpdistribuidora.controller;

import com.api.erpdistribuidora.dto.ImagemRequest;
import com.api.erpdistribuidora.dto.ImagemResponse;
import com.api.erpdistribuidora.service.ProdutoImagemService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/produtos")
@RequiredArgsConstructor
@CrossOrigin(origins = {"http://localhost:5173"})
public class ProdutoImagemController {

    private final ProdutoImagemService service;

    // Envia multipart/form-data com:
    // - part "file" (arquivo)
    // - part "metadata" (JSON: { "produtoId": 123, "nome": "Frente" } ) - opcional
    @PostMapping(value = "/{produtoId}/imagens", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ImagemResponse upload(
            @PathVariable("produtoId") Integer produtoId,
            @RequestPart("file") MultipartFile file,
            @RequestPart(value = "metadata", required = false) ImagemRequest metadata
    ) {
        ImagemRequest req = (metadata == null)
                ? new ImagemRequest(produtoId, null)
                : new ImagemRequest(
                (metadata.produtoId() != null ? metadata.produtoId() : produtoId),
                metadata.nome()
        );

        return service.criar(file, req);
    }

    @DeleteMapping("/{produtoId}/imagens/{imagemId}")
    public void delete(
            @PathVariable("produtoId") Integer produtoId,
            @PathVariable("imagemId") Integer imagemId
    ) {
        service.deletar(imagemId);
    }
}
