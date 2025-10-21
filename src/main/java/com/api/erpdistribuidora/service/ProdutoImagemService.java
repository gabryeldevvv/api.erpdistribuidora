package com.api.erpdistribuidora.service;

import com.api.erpdistribuidora.dto.ImagemRequest;
import com.api.erpdistribuidora.dto.ImagemResponse;
import com.api.erpdistribuidora.dto.UploadResponse;
import com.api.erpdistribuidora.model.Produto;
import com.api.erpdistribuidora.model.ProdutoImagem;
import com.api.erpdistribuidora.repository.ProdutoImagemRepository;
import com.api.erpdistribuidora.repository.ProdutoRepository;
import com.api.erpdistribuidora.service.storage.SupabaseStorageService;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ProdutoImagemService {

    private final ProdutoRepository produtoRepository;
    private final ProdutoImagemRepository imagemRepository;
    private final SupabaseStorageService storage;

    @Transactional(readOnly = true)
    public List<ImagemResponse> listarTodas() {
        return imagemRepository.findAll()
                .stream()
                .map(img -> new ImagemResponse(
                        img.getId(),
                        img.getNome(),
                        img.getUrl(),
                        img.getPath(),
                        img.getProduto().getId()
                ))
                .toList();
    }

    @Transactional
    public ImagemResponse criar(MultipartFile file, ImagemRequest req) {
        Produto produto = produtoRepository.findById(req.produtoId())
                .orElseThrow(() -> new EntityNotFoundException("Produto não encontrado"));

        String destino = "produtos/%d".formatted(produto.getId());
        UploadResponse uploaded = storage.uploadImage(file, destino);

        String nome = (req.nome() != null && !req.nome().isBlank())
                ? req.nome()
                : file.getOriginalFilename();

        ProdutoImagem entity = ProdutoImagem.builder()
                .produto(produto)
                .nome(nome)
                .url(uploaded.url())
                .path(uploaded.path())
                .build();

        ProdutoImagem salvo = imagemRepository.save(entity);

        return new ImagemResponse(
                salvo.getId(),
                salvo.getNome(),
                salvo.getUrl(),
                salvo.getPath(),
                produto.getId()
        );
    }

    @Transactional
    public void deletar(Integer imagemId) {
        ProdutoImagem img = imagemRepository.findById(imagemId)
                .orElseThrow(() -> new EntityNotFoundException("Imagem não encontrada"));

        boolean ok = storage.deleteImage(img.getPath());
        if (!ok) throw new IllegalStateException("Falha ao apagar arquivo no storage");

        imagemRepository.delete(img);
    }
}
