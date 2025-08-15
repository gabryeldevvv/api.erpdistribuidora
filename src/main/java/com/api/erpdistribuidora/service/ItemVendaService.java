package com.api.erpdistribuidora.service;

import com.api.erpdistribuidora.dto.ItemVendaRequestDTO;
import com.api.erpdistribuidora.dto.ItemVendaResponseDTO;
import com.api.erpdistribuidora.exception.ProdutoNaoEncontradoException;
import com.api.erpdistribuidora.exception.VendaNaoEncontradaException;
import com.api.erpdistribuidora.mapper.ItemVendaMapper;
import com.api.erpdistribuidora.model.ItemVenda;
import com.api.erpdistribuidora.model.Produto;
import com.api.erpdistribuidora.model.Venda;
import com.api.erpdistribuidora.repository.ItemVendaRepository;
import com.api.erpdistribuidora.repository.ProdutoRepository;
import com.api.erpdistribuidora.repository.VendaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class ItemVendaService {

    private final ItemVendaRepository itemVendaRepository;
    private final ProdutoRepository produtoRepository;
    private final VendaRepository vendaRepository;
    private final ItemVendaMapper itemVendaMapper;

    public ItemVendaResponseDTO adicionarItem(Long idVenda, ItemVendaRequestDTO dto) {
        Produto produto = produtoRepository.findById(dto.getIdProduto())
                .orElseThrow(() -> new ProdutoNaoEncontradoException(dto.getIdProduto()));
        Venda venda = vendaRepository.findById(idVenda)
                .orElseThrow(() -> new VendaNaoEncontradaException(idVenda));

        ItemVenda item = itemVendaMapper.toEntity(dto, produto, venda);
        item = itemVendaRepository.save(item);
        return itemVendaMapper.toResponseDTO(item);
    }
}
