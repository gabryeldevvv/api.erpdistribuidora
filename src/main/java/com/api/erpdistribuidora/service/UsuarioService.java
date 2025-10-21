package com.api.erpdistribuidora.service;

import com.api.erpdistribuidora.dto.UsuarioRequestDTO;
import com.api.erpdistribuidora.dto.UsuarioResponseDTO;
import com.api.erpdistribuidora.exception.UsuarioNaoEncontradoException;
import com.api.erpdistribuidora.mapper.UsuarioMapper;
import com.api.erpdistribuidora.model.Usuario;
import com.api.erpdistribuidora.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class UsuarioService {

    private final UserRepository repository;
    private final UsuarioMapper mapper;

    @Transactional(readOnly = true)
    public List<UsuarioResponseDTO> listar() {
        return mapper.toResponseDTOList(repository.findAll());
    }

    @Transactional(readOnly = true)
    public UsuarioResponseDTO buscar(Long id) {
        Usuario u = repository.findById(id)
                .orElseThrow(() -> new UsuarioNaoEncontradoException(id));
        return mapper.toResponseDTO(u);
    }

    public UsuarioResponseDTO criar(UsuarioRequestDTO dto) {
        if (repository.existsByEmail(dto.getEmail())) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "email já cadastrado");
        }
        Usuario entity = mapper.toEntity(dto);
        try {
            Usuario saved = repository.save(entity);
            return mapper.toResponseDTO(saved);
        } catch (DataIntegrityViolationException e) {
            // fallback de integridade (unique, not-null, etc.)
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "dados inválidos: " + e.getMostSpecificCause().getMessage());
        }
    }

    public UsuarioResponseDTO atualizar(Long id, UsuarioRequestDTO dto) {
        Usuario existente = repository.findById(id)
                .orElseThrow(() -> new UsuarioNaoEncontradoException(id));
        mapper.updateEntityFromDto(dto, existente);

        try {
            Usuario saved = repository.save(existente);
            return mapper.toResponseDTO(saved);
        } catch (DataIntegrityViolationException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "dados inválidos: " + e.getMostSpecificCause().getMessage());
        }
    }

    public void remover(Long id) {
        if (!repository.existsById(id)) {
            throw new UsuarioNaoEncontradoException(id);
        }
        repository.deleteById(id);
    }
}
