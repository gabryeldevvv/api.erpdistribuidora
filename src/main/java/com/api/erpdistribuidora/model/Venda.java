package com.api.erpdistribuidora.model;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;
import java.util.List;

@Entity
@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
@Builder
@Table(
        name = "venda",
        indexes = { @Index(name = "idx_venda_status", columnList = "status") }
)
public class Venda {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_venda")
    private Long id;

    @CreationTimestamp
    @Column(name = "data_venda", updatable = false)
    private LocalDateTime dataVenda;

    @Builder.Default
    @Column(nullable = false, length = 20)
    private String status = "pendente";

    @Builder.Default
    @Column(name = "estoque_processado", nullable = false)
    private boolean estoqueProcessado = false;

    @Column(columnDefinition = "TEXT")
    private String observacoes;

    @OneToMany(mappedBy = "venda", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ItemVenda> itens;
}
