package com.oficina.api.model;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Entity
public class OrdemServico {

    // Etapas exibidas ao cliente, na ordem em que acontecem
    public static final List<String> STATUS = List.of(
            "Aguardando avaliação",
            "Em orçamento",
            "Aguardando aprovação",
            "Em serviço",
            "Pronto para retirada",
            "Entregue");

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    private Cliente cliente;

    @ManyToOne
    private Veiculo veiculo;

    @ManyToMany
    @JoinTable(
            name = "ordem_servico_servico",
            joinColumns = @JoinColumn(name = "ordem_servico_id"),
            inverseJoinColumns = @JoinColumn(name = "servico_id")
    )
    private List<Servico> servicos;

    private String observacoes;
    private String status;
    private LocalDate dataCriacao;
    private BigDecimal valorTotal;

    private LocalDate previsaoEntrega;

    private LocalDateTime dataAtualizacao;

    public OrdemServico() {
        this.dataCriacao = LocalDate.now();
    }

    @PrePersist
    @PreUpdate
    public void antesDeSalvar() {
        if (status == null) {
            status = STATUS.get(0);
        }
        dataAtualizacao = LocalDateTime.now();
    }

    // Última etapa: o carro já foi devolvido e a ordem vai para o histórico do cliente
    public boolean isConcluida() {
        return STATUS.get(STATUS.size() - 1).equals(status);
    }

    // Posição do status atual na lista STATUS (-1 se for um status fora da lista)
    public int getEtapaAtual() {
        return STATUS.indexOf(status);
    }

    // Getters e Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Cliente getCliente() { return cliente; }
    public void setCliente(Cliente cliente) { this.cliente = cliente; }

    public Veiculo getVeiculo() { return veiculo; }
    public void setVeiculo(Veiculo veiculo) { this.veiculo = veiculo; }

    public List<Servico> getServicos() { return servicos; }
    public void setServicos(List<Servico> servicos) { this.servicos = servicos; }

    public String getObservacoes() { return observacoes; }
    public void setObservacoes(String observacoes) { this.observacoes = observacoes; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public LocalDate getDataCriacao() { return dataCriacao; }
    public void setDataCriacao(LocalDate dataCriacao) { this.dataCriacao = dataCriacao; }

    public BigDecimal getValorTotal() { return valorTotal; }
    public void setValorTotal(BigDecimal valorTotal) { this.valorTotal = valorTotal; }

    public LocalDate getPrevisaoEntrega() { return previsaoEntrega; }
    public void setPrevisaoEntrega(LocalDate previsaoEntrega) { this.previsaoEntrega = previsaoEntrega; }

    public LocalDateTime getDataAtualizacao() { return dataAtualizacao; }
    public void setDataAtualizacao(LocalDateTime dataAtualizacao) { this.dataAtualizacao = dataAtualizacao; }
}
