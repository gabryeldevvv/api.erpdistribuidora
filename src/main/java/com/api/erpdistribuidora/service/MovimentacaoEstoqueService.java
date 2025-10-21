package com.api.erpdistribuidora.service;

import com.api.erpdistribuidora.dto.MovimentacaoEstoqueRequestDTO;
import com.api.erpdistribuidora.model.MovimentacaoEstoque;
import com.api.erpdistribuidora.model.Produto;
import com.api.erpdistribuidora.model.Usuario;
import com.api.erpdistribuidora.repository.MovimentacaoEstoqueRepository;
import com.api.erpdistribuidora.repository.ProdutoRepository;
import com.api.erpdistribuidora.repository.UserRepository;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true )
public class MovimentacaoEstoqueService {

    private final MovimentacaoEstoqueRepository movimentacaoRepository;
    private final ProdutoRepository produtoRepository;
    private final UserRepository userRepository;

    public List<MovimentacaoEstoque> listarPorProduto(Long idProduto) {
        return movimentacaoRepository.findByProdutoIdOrderByDataMovimentacaoDesc(idProduto);
    }

    @Transactional
    public MovimentacaoEstoque criar(MovimentacaoEstoqueRequestDTO dto) {
        Produto produto = produtoRepository.findById(dto.getIdProduto())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "Produto inválido"));

        Usuario usuario = userRepository.findById(dto.getIdUsuario())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "Usuário inválido"));

        MovimentacaoEstoque novaMovimentacao = MovimentacaoEstoque.builder()
                .produto(produto)
                .tipo(dto.getTipo())
                .quantidade(dto.getQuantidade())
                .referencia(dto.getReferencia())
                .usuario(usuario)
                .build();

        return movimentacaoRepository.save(novaMovimentacao);
    }
}
