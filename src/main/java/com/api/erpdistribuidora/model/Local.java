package com.api.erpdistribuidora.model;

import jakarta.persistence.*;
import lombok.*;

import java.util.List;

@Entity
@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
@Builder
@Table(
        name = "local_estoque",
        uniqueConstraints = @UniqueConstraint(name = "uq_local_nome", columnNames = {"nome"})
)
public class Local {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_local")
    private Long id;

    @Column(nullable = false, length = 80)
    private String nome;

    @Column(length = 255)
    private String descricao;

    @OneToMany(mappedBy = "local")
    private List<Estoque> estoques;
}
