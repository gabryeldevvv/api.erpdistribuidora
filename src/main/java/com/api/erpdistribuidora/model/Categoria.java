// src/main/java/com/api/erpdistribuidora/model/Categoria.java
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
        name = "categoria",
        uniqueConstraints = {
                @UniqueConstraint(name = "unq_categoria_id_publico", columnNames = "id_publico")
        },
        indexes = {
                @Index(name = "idx_categoria_pai", columnList = "id_categoria_pai"),
                @Index(name = "idx_categoria_tipo", columnList = "tipo")
        }
)
public class Categoria {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id") // PK
    private Long id;

    @Column(name = "id_publico", nullable = false, length = 50)
    private String idPublico;

    @Column(name = "nome", nullable = false, length = 100)
    private String nome;

    @Enumerated(EnumType.STRING)
    @Column(name = "tipo", nullable = false, length = 20)
    private TipoCategoria tipo;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_categoria_pai",
            foreignKey = @ForeignKey(name = "fk_categoria_pai"))
    private Categoria categoriaPai;

    @OneToMany(mappedBy = "categoriaPai")
    private List<Categoria> subcategorias;
}
