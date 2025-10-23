// src/main/java/com/api/erpdistribuidora/service/MovimentacaoEstoqueService.java
package com.api.erpdistribuidora.service;

import com.api.erpdistribuidora.dto.MovimentacaoEstoqueRequestDTO;
import com.api.erpdistribuidora.model.Estoque;
import com.api.erpdistribuidora.model.MovimentacaoEstoque;
import com.api.erpdistribuidora.repository.EstoqueRepository;
import com.api.erpdistribuidora.repository.MovimentacaoEstoqueRepository;
import com.api.erpdistribuidora.repository.ProdutoRepository;
import com.api.erpdistribuidora.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class MovimentacaoEstoqueService {

    private final MovimentacaoEstoqueRepository movimentacaoRepository;
    private final ProdutoRepository produtoRepository;
    private final UserRepository userRepository;
    private final EstoqueRepository estoqueRepository;

    public List<MovimentacaoEstoque> listarTodas() {
        return movimentacaoRepository.findAll();
    }

    public List<MovimentacaoEstoque> listarPorProduto(Long idProduto) {
        return movimentacaoRepository.findByProdutoIdOrderByDataMovimentacaoDesc(idProduto);
    }

    @Transactional // override do readOnly=true de classe
    public MovimentacaoEstoque criar(MovimentacaoEstoqueRequestDTO dto) {
        var produto = produtoRepository.findById(dto.getIdProduto())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "Produto inválido"));

        var usuario = userRepository.findById(dto.getIdUsuario())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "Usuário inválido"));

        // Obtém (ou cria) o registro de estoque do produto
        var estoques = estoqueRepository.findByProdutoId(produto.getId());
        Estoque estoque = estoques.isEmpty()
                ? Estoque.builder().produto(produto).quantidade(0).build()
                : estoques.get(0);

        int atual = estoque.getQuantidade() == null ? 0 : estoque.getQuantidade();
        int q = dto.getQuantidade(); // DTO garante > 0

        switch (dto.getTipo()) {
            case "entrada" -> atual += q;
            case "saida" -> {
                if (atual < q) {
                    throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Saldo insuficiente");
                }
                atual -= q;
            }
            case "ajuste" -> atual = q; // ajuste define o novo saldo
            default -> throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Tipo inválido");
        }

        estoque.setQuantidade(atual);
        estoqueRepository.save(estoque);

        var mov = MovimentacaoEstoque.builder()
                .produto(produto)
                .tipo(dto.getTipo())
                .quantidade(dto.getQuantidade())
                .referencia(dto.getReferencia())
                .usuario(usuario)
                .build();

        return movimentacaoRepository.save(mov);
    }
}
