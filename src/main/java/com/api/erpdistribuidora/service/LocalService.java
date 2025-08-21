package com.api.erpdistribuidora.service;

import com.api.erpdistribuidora.dto.LocalRequestDTO;
import com.api.erpdistribuidora.dto.LocalResponseDTO;
import com.api.erpdistribuidora.exception.LocalNaoEncontradoException;
import com.api.erpdistribuidora.mapper.LocalMapper;
import com.api.erpdistribuidora.model.Local;
import com.api.erpdistribuidora.repository.LocalRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Service
@RequiredArgsConstructor
public class LocalService {

    private final LocalRepository localRepository;
    private final LocalMapper localMapper;

    @Transactional(readOnly = true)
    public List<LocalResponseDTO> listar() {
        return localMapper.toResponseDTOList(localRepository.findAll());
    }

    @Transactional(readOnly = true)
    public LocalResponseDTO buscarPorId(Long id) {
        return localRepository.findById(id)
                .map(localMapper::toResponseDTO)
                .orElseThrow(() -> new LocalNaoEncontradoException(id));
    }

    @Transactional
    public LocalResponseDTO criar(LocalRequestDTO dto) {
        Local entity = localMapper.toEntity(dto);
        try {
            return localMapper.toResponseDTO(localRepository.save(entity));
        } catch (DataIntegrityViolationException e) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Nome de local já existente", e);
        }
    }

    @Transactional
    public LocalResponseDTO atualizar(Long id, LocalRequestDTO dto) {
        Local existente = localRepository.findById(id)
                .orElseThrow(() -> new LocalNaoEncontradoException(id));
        localMapper.updateEntity(existente, dto);
        try {
            return localMapper.toResponseDTO(localRepository.save(existente));
        } catch (DataIntegrityViolationException e) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Nome de local já existente", e);
        }
    }

    @Transactional
    public void remover(Long id) {
        if (!localRepository.existsById(id)) {
            throw new LocalNaoEncontradoException(id);
        }
        try {
            localRepository.deleteById(id);
        } catch (DataIntegrityViolationException e) {
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT,
                    "Não foi possível remover: existem estoques referenciando este local.",
                    e
            );
        }
    }
}
