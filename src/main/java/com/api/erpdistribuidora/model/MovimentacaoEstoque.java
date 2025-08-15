package com.api.erpdistribuidora.model;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Entity
@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
@Builder
@Table(
        name = "movimentacao_estoque",
        indexes = {
                @Index(name = "idx_movimentacao_produto", columnList = "id_produto"),
                @Index(name = "idx_movimentacao_data", columnList = "data_movimentacao")
        }
)
public class MovimentacaoEstoque {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_movimentacao")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_produto", nullable = false)
    private Produto produto;

    @Column(nullable = false, length = 10)
    private String tipo; // entrada | saida | ajuste

    @Column(nullable = false)
    private Integer quantidade;

    @CreationTimestamp
    @Column(name = "data_movimentacao", updatable = false)
    private LocalDateTime dataMovimentacao;

    @Column(length = 100)
    private String referencia;

    @Column(name = "id_usuario")
    private Integer idUsuario;
}
