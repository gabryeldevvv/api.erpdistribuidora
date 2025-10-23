// src/main/java/com/api/erpdistribuidora/service/MovimentacaoEstoqueService.java
package com.api.erpdistribuidora.service;

import com.api.erpdistribuidora.dto.MovimentacaoEstoqueRequestDTO;
import com.api.erpdistribuidora.dto.MovimentacaoEstoqueResponseDTO;
import com.api.erpdistribuidora.mapper.MovimentacaoEstoqueMapper;
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
    private final MovimentacaoEstoqueMapper mapper; // <-- injetado

    public List<MovimentacaoEstoqueResponseDTO> listarTodas() {
        var lista = movimentacaoRepository.findAll();
        return mapper.toResponseDTOList(lista);
    }

    public List<MovimentacaoEstoqueResponseDTO> listarPorProduto(Long idProduto) {
        var lista = movimentacaoRepository.findByProdutoIdOrderByDataMovimentacaoDesc(idProduto);
        return mapper.toResponseDTOList(lista);
    }

    @Transactional
    public MovimentacaoEstoqueResponseDTO criar(MovimentacaoEstoqueRequestDTO dto) {
        var produto = produtoRepository.findById(dto.getIdProduto())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "Produto inválido"));

        var usuario = userRepository.findById(dto.getIdUsuario())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "Usuário inválido"));

        // obtém (ou cria) o estoque do produto
        var estoques = estoqueRepository.findByProdutoId(produto.getId());
        Estoque estoque = estoques.isEmpty()
                ? Estoque.builder().produto(produto).quantidade(0).build()
                : estoques.get(0);

        int atual = estoque.getQuantidade() == null ? 0 : estoque.getQuantidade();
        int q = dto.getQuantidade();

        switch (dto.getTipo()) {
            case "entrada" -> atual += q;
            case "saida" -> {
                if (atual < q) throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Saldo insuficiente");
                atual -= q;
            }
            case "ajuste" -> atual = q;
            default -> throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Tipo inválido");
        }

        estoque.setQuantidade(atual);
        estoqueRepository.save(estoque);

        MovimentacaoEstoque mov = MovimentacaoEstoque.builder()
                .produto(produto)
                .tipo(dto.getTipo())
                .quantidade(dto.getQuantidade())
                .referencia(dto.getReferencia())
                .usuario(usuario)
                .build();

        mov = movimentacaoRepository.save(mov);
        return mapper.toResponseDTO(mov); // <-- service já retorna DTO
    }
}
