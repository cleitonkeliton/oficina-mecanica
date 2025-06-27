package com.oficina.api.model;

import jakarta.persistence.*;

@Entity
@Table(name = "servicos")
public class Servico {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String descricao;

    private Double preco;

    // Construtor padrão
    public Servico() {}

    // Construtor com campos
    public Servico(Long id, String descricao, Double preco) {
        this.id = id;
        this.descricao = descricao;
        this.preco = preco;
    }

    // Getters e Setters

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }

    public Double getPreco() {
        return preco;
    }

    public void setPreco(Double preco) {
        this.preco = preco;
    }
}
