package com.api.erpdistribuidora.model;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;

@Entity
@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
@Builder
@Table(
        name = "item_venda",
        indexes = {
                @Index(name = "idx_item_venda_produto", columnList = "id_produto"),
                @Index(name = "idx_item_venda_venda", columnList = "id_venda")
        }
)
public class ItemVenda {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_item_venda")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_venda", nullable = false)
    private Venda venda;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_produto", nullable = false)
    private Produto produto;

    @Column(nullable = false)
    private Integer quantidade;

    @Column(name = "preco_unitario", nullable = false, precision = 10, scale = 2)
    private BigDecimal precoUnitario;

    @Builder.Default
    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal desconto = BigDecimal.ZERO;
}
