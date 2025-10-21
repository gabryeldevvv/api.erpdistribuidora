package com.api.erpdistribuidora.controller;

import com.api.erpdistribuidora.dto.ImagemRequest;
import com.api.erpdistribuidora.dto.ImagemResponse;
import com.api.erpdistribuidora.service.ProdutoImagemService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/api/produtos")
@RequiredArgsConstructor
@CrossOrigin(origins = {"http://localhost:5173"})
public class ProdutoImagemController {

    private final ProdutoImagemService service;

    @GetMapping("/imagens")
    public List<ImagemResponse> listAll() {
        return service.listarTodas();
    }

    @PostMapping(value = "/{produtoId}/imagens", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ImagemResponse upload(
            @PathVariable("produtoId") Long produtoId,
            @RequestPart("file") MultipartFile file,
            @RequestPart("metadata") ImagemRequest metadata
    ) {
        ImagemRequest req = new ImagemRequest(
                produtoId,
                metadata.nome()
        );

        return service.criar(file, req);
    }

    @DeleteMapping("/imagens/{imagemId}")
    public void delete(
            @PathVariable("imagemId") Integer imagemId
    ) {
        service.deletar(imagemId);
    }
}