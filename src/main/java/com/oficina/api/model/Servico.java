package com.oficina.api.model;

import jakarta.persistence.*;

@Entity
@Table(name = "servicos")
public class Servico {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Nome exibido no site (ex.: "Troca de óleo")
    private String nome;

    @Column(length = 1000)
    private String descricao;

    private Double preco;

    // Texto livre, ex.: "1 hora", "1 dia"
    private String tempoEstimado;

    // Serviços inativos não aparecem na página principal
    private Boolean ativo = true;

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

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
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

    public String getTempoEstimado() {
        return tempoEstimado;
    }

    public void setTempoEstimado(String tempoEstimado) {
        this.tempoEstimado = tempoEstimado;
    }

    public Boolean getAtivo() {
        return ativo;
    }

    public void setAtivo(Boolean ativo) {
        this.ativo = ativo;
    }

    // Registros antigos não têm a coluna "ativo" preenchida: tratamos como ativos
    public boolean isVisivel() {
        return ativo == null || ativo;
    }
}
