package com.api.erpdistribuidora.model;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
@Builder
@Table(name = "produto")
public class Produto {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_produto")
    private Long id;

    @Column(nullable = false, length = 100)
    private String nome;

    @Column(columnDefinition = "TEXT")
    private String descricao;

    @Column(name = "preco_unitario", nullable = false, precision = 10, scale = 2)
    private BigDecimal precoUnitario;

    @Column(name = "unidade_medida", nullable = false, length = 10)
    private String unidadeMedida;

    @Column(name = "data_validade")
    private LocalDate dataValidade;

    @CreationTimestamp
    @Column(name = "data_cadastro", updatable = false)
    private LocalDateTime dataCadastro;

    @Builder.Default
    @Column(nullable = false)
    private boolean ativo = true;

    @OneToMany(mappedBy = "produto")
    private List<Estoque> estoques;

    @OneToMany(mappedBy = "produto")
    private List<ItemVenda> itensVenda;

    @OneToMany(mappedBy = "produto")
    private List<MovimentacaoEstoque> movimentacoes;

    @OneToMany(mappedBy = "produto", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<ProdutoImagem> imagens = new ArrayList<>();

    public void addImagem(ProdutoImagem img) {
        imagens.add(img);
        img.setProduto(this);
    }

    public void removeImagem(ProdutoImagem img) {
        imagens.remove(img);
        img.setProduto(null);
    }
}
