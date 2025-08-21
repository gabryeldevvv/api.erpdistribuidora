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
                name = "uq_estoque_produto_local",
                columnNames = {"id_produto", "id_local"}
        ),
        indexes = {
                @Index(name = "idx_estoque_produto", columnList = "id_produto"),
                @Index(name = "idx_estoque_local", columnList = "id_local")
        }
)
public class Estoque {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_estoque")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_produto", nullable = false)
    private Produto produto;

    @Column(nullable = false)
    private Integer quantidade;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_local", nullable = false)
    private Local local;

    @Column(name = "ultima_atualizacao")
    private LocalDateTime ultimaAtualizacao;

    @PrePersist
    @PreUpdate
    private void touchTimestamp() {
        this.ultimaAtualizacao = LocalDateTime.now();
    }
}
