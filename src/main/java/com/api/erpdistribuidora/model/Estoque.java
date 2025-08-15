package com.api.erpdistribuidora.model;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
@Builder
@Table(
        name = "estoque",
        uniqueConstraints = @UniqueConstraint(
                name = "uq_estoque_produto_localizacao",
                columnNames = {"id_produto", "localizacao"}
        ),
        indexes = { @Index(name = "idx_estoque_produto", columnList = "id_produto") }
)
public class Estoque {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_estoque")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_produto", nullable = false)
    private Produto produto;

    @Builder.Default
    @Column(nullable = false)
    private Integer quantidade = 0;

    @Column(nullable = false, length = 50)
    private String localizacao;

    @Column(name = "ultima_atualizacao")
    private LocalDateTime ultimaAtualizacao;

    @PrePersist
    @PreUpdate
    private void touchTimestamp() {
        this.ultimaAtualizacao = LocalDateTime.now();
    }
}

