// src/main/java/com/api/erpdistribuidora/repository/CategoriaRepository.java
package com.api.erpdistribuidora.repository;

import com.api.erpdistribuidora.model.Categoria;
import com.api.erpdistribuidora.model.TipoCategoria;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CategoriaRepository extends JpaRepository<Categoria, Long> {
    Optional<Categoria> findByIdPublico(String idPublico);
    boolean existsByIdPublico(String idPublico);
    long countByCategoriaPaiId(Long idPai);
    boolean existsByIdAndCategoriaPaiId(Long id, Long idPai);
    Optional<Categoria> findByIdAndTipo(Long id, TipoCategoria tipo);
}
