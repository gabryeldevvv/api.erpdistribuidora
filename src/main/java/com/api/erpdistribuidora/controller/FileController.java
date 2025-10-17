package com.api.erpdistribuidora.controller;

import com.api.erpdistribuidora.dto.UploadResponse;
import com.api.erpdistribuidora.service.storage.SupabaseStorageService;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

// NOVO:
import com.api.erpdistribuidora.dto.ImagemRequest;
import com.api.erpdistribuidora.dto.ImagemResponse;
import com.api.erpdistribuidora.service.ProdutoImagemService;

@CrossOrigin(origins = {"http://localhost:5173"})
@RestController
@RequestMapping("/api/files")
public class FileController {

    private final SupabaseStorageService storageService;

    // NOVO:
    private final ProdutoImagemService produtoImagemService;

    // troque o construtor para injetar o novo service
    public FileController(SupabaseStorageService storageService,
                          ProdutoImagemService produtoImagemService) {
        this.storageService = storageService;
        this.produtoImagemService = produtoImagemService;
    }

    @PostMapping(value = "/upload-image", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public UploadResponse upload(@RequestPart("file") MultipartFile file) {
        return storageService.uploadImage(file);
    }

    // NOVO: upload + registro no banco
    @PostMapping(value = "/upload-image-with-record", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ImagemResponse uploadWithRecord(
            @RequestPart("file") MultipartFile file,
            @RequestParam("produtoId") Long produtoId,
            @RequestParam(value = "nome", required = false) String nome
    ) {
        return produtoImagemService.criar(file, new ImagemRequest(produtoId, nome));
    }

    // NOVO: deletar registro + arquivo
    @DeleteMapping("/images/{imagemId}")
    public void deleteImageRecord(@PathVariable("imagemId") Integer imagemId) {
        produtoImagemService.deletar(imagemId);
    }
}
