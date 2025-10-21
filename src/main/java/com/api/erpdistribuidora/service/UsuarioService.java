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
@Transactional(readOnly = true)
public class UsuarioService {

    private final UserRepository userRepository;
    private final UsuarioMapper usuarioMapper;

    public List<UsuarioResponseDTO> listar() {
        return usuarioMapper.toResponseDTOList(userRepository.findAll());
    }

    public UsuarioResponseDTO buscarPorId(Long id) {
        Usuario usuario = userRepository.findById(id)
                .orElseThrow(() -> new UsuarioNaoEncontradoException(id));
        return usuarioMapper.toResponseDTO(usuario);
    }

    @Transactional
    public UsuarioResponseDTO criar(UsuarioRequestDTO dto) {
        if (userRepository.findByEmail(dto.getEmail()).isPresent()) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Usuário com este email já existe.");
        }
        Usuario usuario = usuarioMapper.toEntity(dto);
        // TODO: Criptografar senha antes de salvar
        return usuarioMapper.toResponseDTO(userRepository.save(usuario));
    }

    @Transactional
    public UsuarioResponseDTO atualizar(Long id, UsuarioRequestDTO dto) {
        Usuario usuarioExistente = userRepository.findById(id)
                .orElseThrow(() -> new UsuarioNaoEncontradoException(id));

        userRepository.findByEmail(dto.getEmail()).ifPresent(u -> {
            if (!u.getId().equals(id)) {
                throw new ResponseStatusException(HttpStatus.CONFLICT, "Outro usuário com este email já existe.");
            }
        });

        usuarioMapper.updateEntityFromDto(dto, usuarioExistente);
        // TODO: Criptografar nova senha se for alterada
        return usuarioMapper.toResponseDTO(userRepository.save(usuarioExistente));
    }

    @Transactional
    public void remover(Long id) {
        if (!userRepository.existsById(id)) {
            throw new UsuarioNaoEncontradoException(id);
        }
        try {
            userRepository.deleteById(id);
        } catch (DataIntegrityViolationException e) {
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT,
                    "Não foi possível remover o usuário: ele está vinculado a outras entidades.",
                    e
            );
        }
    }
}

