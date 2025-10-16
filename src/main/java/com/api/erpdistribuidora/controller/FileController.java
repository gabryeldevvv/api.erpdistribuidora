package com.api.erpdistribuidora.controller;

import com.api.erpdistribuidora.dto.UploadResponse;
import com.api.erpdistribuidora.service.storage.SupabaseStorageService;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@CrossOrigin(origins = {"http://localhost:5173"}) // habilite se o front estiver em outra origem
@RestController
@RequestMapping("/api/files")
public class FileController {

    private final SupabaseStorageService storageService;

    public FileController(SupabaseStorageService storageService) {
        this.storageService = storageService;
    }

    @PostMapping(value = "/upload-image", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public UploadResponse upload(@RequestPart("file") MultipartFile file) {
        return storageService.uploadImage(file);
    }
}
