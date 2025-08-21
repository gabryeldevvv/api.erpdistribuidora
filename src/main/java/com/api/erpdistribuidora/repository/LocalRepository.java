package com.api.erpdistribuidora.repository;

import com.api.erpdistribuidora.model.Local;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface LocalRepository extends JpaRepository<Local, Long> {
    Optional<Local> findByNome(String nome);
}
