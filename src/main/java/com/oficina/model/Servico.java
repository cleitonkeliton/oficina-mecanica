package com.oficina.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;

/** Serviço oferecido pela oficina, com o preço exibido no site. */
@Entity
@Table(name = "servicos")
public class Servico {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Informe o nome do serviço")
    @Size(max = 100)
    private String nome;

    @Size(max = 500)
    @Column(length = 500)
    private String descricao;

    @NotNull(message = "Informe o preço")
    @DecimalMin(value = "0.00", message = "O preço não pode ser negativo")
    @Digits(integer = 8, fraction = 2, message = "Preço inválido")
    @Column(precision = 10, scale = 2)
    private BigDecimal preco;

    /** Quando marcado, o site mostra "a partir de R$ ...". */
    private boolean precoAPartirDe;

    /** Ex.: "1 hora", "1 dia". */
    @Size(max = 50)
    private String tempoEstimado;

    /** Só serviços ativos aparecem no site e na abertura de ordens. */
    private boolean ativo = true;

    // Construtor padrão
    public Servico() {}

    // Construtor com campos
    public Servico(String nome, String descricao, BigDecimal preco, boolean precoAPartirDe, String tempoEstimado) {
        this.nome = nome;
        this.descricao = descricao;
        this.preco = preco;
        this.precoAPartirDe = precoAPartirDe;
        this.tempoEstimado = tempoEstimado;
    }

    // Getters e Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getNome() { return nome; }
    public void setNome(String nome) { this.nome = nome; }

    public String getDescricao() { return descricao; }
    public void setDescricao(String descricao) { this.descricao = descricao; }

    public BigDecimal getPreco() { return preco; }
    public void setPreco(BigDecimal preco) { this.preco = preco; }

    public boolean isPrecoAPartirDe() { return precoAPartirDe; }
    public void setPrecoAPartirDe(boolean precoAPartirDe) { this.precoAPartirDe = precoAPartirDe; }

    public String getTempoEstimado() { return tempoEstimado; }
    public void setTempoEstimado(String tempoEstimado) { this.tempoEstimado = tempoEstimado; }

    public boolean isAtivo() { return ativo; }
    public void setAtivo(boolean ativo) { this.ativo = ativo; }
}
