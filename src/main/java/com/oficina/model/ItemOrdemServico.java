package com.oficina.model;

import jakarta.persistence.*;

import java.math.BigDecimal;

/**
 * Serviço ou peça lançado na ordem. Guarda a descrição e o valor do momento,
 * assim mudar o preço no site não altera ordens antigas.
 */
@Entity
@Table(name = "itens_ordem_servico")
public class ItemOrdemServico {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional = false)
    @JoinColumn(name = "ordem_id", nullable = false)
    private OrdemServico ordem;

    /** Serviço do catálogo de origem (vazio quando é uma peça ou item avulso). */
    @ManyToOne
    @JoinColumn(name = "servico_id")
    private Servico servico;

    @Column(length = 150, nullable = false)
    private String descricao;

    @Column(nullable = false)
    private int quantidade = 1;

    @Column(precision = 10, scale = 2, nullable = false)
    private BigDecimal valorUnitario = BigDecimal.ZERO;

    public ItemOrdemServico() {}

    public ItemOrdemServico(Servico servico, String descricao, int quantidade, BigDecimal valorUnitario) {
        this.servico = servico;
        this.descricao = descricao;
        this.quantidade = quantidade;
        this.valorUnitario = valorUnitario;
    }

    public static ItemOrdemServico doCatalogo(Servico servico) {
        return new ItemOrdemServico(servico, servico.getNome(), 1, servico.getPreco());
    }

    public BigDecimal getSubtotal() {
        return valorUnitario.multiply(BigDecimal.valueOf(quantidade));
    }

    // Getters e Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public OrdemServico getOrdem() { return ordem; }
    public void setOrdem(OrdemServico ordem) { this.ordem = ordem; }

    public Servico getServico() { return servico; }
    public void setServico(Servico servico) { this.servico = servico; }

    public String getDescricao() { return descricao; }
    public void setDescricao(String descricao) { this.descricao = descricao; }

    public int getQuantidade() { return quantidade; }
    public void setQuantidade(int quantidade) { this.quantidade = quantidade; }

    public BigDecimal getValorUnitario() { return valorUnitario; }
    public void setValorUnitario(BigDecimal valorUnitario) { this.valorUnitario = valorUnitario; }
}
