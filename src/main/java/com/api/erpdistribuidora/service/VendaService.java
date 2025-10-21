package com.api.erpdistribuidora.service;

import com.api.erpdistribuidora.dto.ItemVendaRequestDTO;
import com.api.erpdistribuidora.dto.VendaRequestDTO;
import com.api.erpdistribuidora.dto.VendaResponseDTO;
import com.api.erpdistribuidora.exception.EstoqueInsuficienteException;
import com.api.erpdistribuidora.exception.ProdutoNaoEncontradoException;
import com.api.erpdistribuidora.exception.VendaNaoEncontradaException;
import com.api.erpdistribuidora.mapper.ItemVendaMapper;
import com.api.erpdistribuidora.mapper.VendaMapper;
import com.api.erpdistribuidora.model.Estoque;
import com.api.erpdistribuidora.model.ItemVenda;
import com.api.erpdistribuidora.model.MovimentacaoEstoque;
import com.api.erpdistribuidora.model.Produto;
import com.api.erpdistribuidora.model.Venda;
import com.api.erpdistribuidora.repository.EstoqueRepository;
import com.api.erpdistribuidora.repository.ItemVendaRepository;
import com.api.erpdistribuidora.repository.MovimentacaoEstoqueRepository;
import com.api.erpdistribuidora.repository.ProdutoRepository;
import com.api.erpdistribuidora.repository.VendaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

@Service
@RequiredArgsConstructor
@Transactional
public class VendaService {

    private final VendaRepository vendaRepository;
    private final ProdutoRepository produtoRepository;
    private final ItemVendaRepository itemVendaRepository;
    private final EstoqueRepository estoqueRepository;
    private final MovimentacaoEstoqueRepository movimentacaoRepository;

    private final ItemVendaMapper itemVendaMapper;
    private final VendaMapper vendaMapper;

    public VendaResponseDTO criar(VendaRequestDTO dto ) {
        Venda venda = Venda.builder()
                .status(dto.getStatus() != null ? dto.getStatus() : "pendente")
                .observacoes(dto.getObservacoes())
                .estoqueProcessado(false)
                .build();

        venda = vendaRepository.save(venda);

        if (dto.getItens() != null && !dto.getItens().isEmpty()) {
            for (ItemVendaRequestDTO itemDto : dto.getItens()) {
                Produto produto = produtoRepository.findById(itemDto.getIdProduto())
                        .orElseThrow(() -> new ProdutoNaoEncontradoException(itemDto.getIdProduto()));
                ItemVenda item = itemVendaMapper.toEntity(itemDto, produto, venda);
                itemVendaRepository.save(item);
            }
        }

        if ("concluída".equalsIgnoreCase(venda.getStatus()) && !venda.isEstoqueProcessado()) {
            processarEstoque(venda);
            venda.setEstoqueProcessado(true);
            vendaRepository.save(venda);
        }

        // ⚠️ Evitar capturar 'venda' em lambda durante atribuição:
        Long vendaId = venda.getId();
        Venda recarregada = vendaRepository.findById(vendaId)
                .orElseThrow(() -> new VendaNaoEncontradaException(vendaId));

        return vendaMapper.toResponseDTO(recarregada);
    }

    public VendaResponseDTO atualizarStatus(Long idVenda, String status) {
        Venda venda = vendaRepository.findById(idVenda)
                .orElseThrow(() -> new VendaNaoEncontradaException(idVenda));

        venda.setStatus(status);

        if ("concluída".equalsIgnoreCase(status) && !venda.isEstoqueProcessado()) {
            processarEstoque(venda);
            venda.setEstoqueProcessado(true);
        }

        venda = vendaRepository.save(venda);
        return vendaMapper.toResponseDTO(venda);
    }

    @Transactional(readOnly = true)
    public VendaResponseDTO buscarPorId(Long idVenda) {
        Venda venda = vendaRepository.findById(idVenda)
                .orElseThrow(() -> new VendaNaoEncontradaException(idVenda));
        return vendaMapper.toResponseDTO(venda);
    }

    @Transactional(readOnly = true)
    public List<VendaResponseDTO> listarTodas() {
        List<Venda> vendas = vendaRepository.findAll();
        List<VendaResponseDTO> respostas = new ArrayList<>(vendas.size());
        for (Venda v : vendas) {
            respostas.add(vendaMapper.toResponseDTO(v));
        }
        return respostas;
    }

    public void deletar(Long idVenda) {
        if (!vendaRepository.existsById(idVenda)) {
            throw new VendaNaoEncontradaException(idVenda);
        }
        try {
            vendaRepository.deleteById(idVenda);
        } catch (DataIntegrityViolationException e) {
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT,
                    "Não foi possível remover a venda: existem itens de venda vinculados.",
                    e
            );
        }
    }

    /**
     * Replica a trigger SQL: valida disponibilidade e dá baixa com movimentação.
     * Evita capturar variáveis não-final em lambdas.
     */
    private void processarEstoque(Venda venda) {
        // ⚠️ Evitar capturar 'venda' em lambda durante atribuição:
        Long vendaId = venda.getId();
        Venda vendaAtual = vendaRepository.findById(vendaId)
                .orElseThrow(() -> new VendaNaoEncontradaException(vendaId));

        // 1) Validar disponibilidade total por produto (somando todas localizações)
        for (ItemVenda item : vendaAtual.getItens()) {
            List<Estoque> estoquesProduto = estoqueRepository.findByProdutoId(item.getProduto().getId());
            int disponivel = 0;
            for (Estoque e : estoquesProduto) {
                disponivel += e.getQuantidade();
            }
            if (disponivel < item.getQuantidade()) {
                throw new EstoqueInsuficienteException(
                        item.getProduto().getId(), disponivel, item.getQuantidade()
                );
            }
        }

        // 2) Baixa — consumir dos estoques com maior quantidade primeiro
        for (ItemVenda item : vendaAtual.getItens()) {
            int restante = item.getQuantidade();

            List<Estoque> pilhas = estoqueRepository.findByProdutoIdOrderByQuantidadeDesc(item.getProduto().getId());
            // Garantir ordenação desc mesmo se a query mudar
            List<Estoque> ordenada = new ArrayList<>(pilhas);
            ordenada.sort(Comparator.comparing(Estoque::getQuantidade).reversed());

            for (Estoque est : ordenada) {
                if (restante <= 0) break;
                int usar = Math.min(est.getQuantidade(), restante);
                if (usar > 0) {
                    est.setQuantidade(est.getQuantidade() - usar);
                    estoqueRepository.save(est);

                    MovimentacaoEstoque mov = MovimentacaoEstoque.builder()
                            .produto(item.getProduto())
                            .tipo("saida")
                            .quantidade(usar)
                            .referencia("Venda #" + vendaAtual.getId())
                            .build();
                    movimentacaoRepository.save(mov);

                    restante -= usar;
                }
            }
        }
    }
}
