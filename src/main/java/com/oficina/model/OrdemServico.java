package com.oficina.model;

import jakarta.persistence.*;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Passagem de um carro pela oficina: entrada, serviços feitos, situação atual e histórico.
 */
@Entity
@Table(name = "ordens_servico")
public class OrdemServico {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /** Impede que duas pessoas salvem a mesma ordem ao mesmo tempo e uma apague a mudança da outra. */
    @Version
    private long versao;

    @ManyToOne(optional = false)
    @JoinColumn(name = "veiculo_id", nullable = false)
    private Veiculo veiculo;

    /** Dono do carro no momento da entrada (o carro pode trocar de dono depois). */
    @ManyToOne(optional = false)
    @JoinColumn(name = "cliente_id", nullable = false)
    private Cliente cliente;

    @Enumerated(EnumType.STRING)
    @JdbcTypeCode(SqlTypes.VARCHAR)
    @Column(length = 30, nullable = false)
    private StatusServico status = StatusServico.AGUARDANDO_AVALIACAO;

    /** O que o cliente relatou na entrada. */
    @Column(columnDefinition = "TEXT")
    private String descricaoProblema;

    /** Observações da oficina, visíveis para o cliente. */
    @Column(columnDefinition = "TEXT")
    private String observacoes;

    private Integer quilometragem;

    @Column(nullable = false, updatable = false)
    private LocalDateTime dataEntrada;

    private LocalDate previsaoEntrega;

    /** Quando ficou pronto. */
    private LocalDateTime dataConclusao;

    /** Quando o cliente retirou. */
    private LocalDateTime dataEntrega;

    @Column(precision = 12, scale = 2, nullable = false)
    private BigDecimal valorTotal = BigDecimal.ZERO;

    @OneToMany(mappedBy = "ordem", cascade = CascadeType.ALL, orphanRemoval = true)
    @OrderBy("id ASC")
    private List<ItemOrdemServico> itens = new ArrayList<>();

    @OneToMany(mappedBy = "ordem", cascade = CascadeType.ALL, orphanRemoval = true)
    @OrderBy("dataHora ASC, id ASC")
    private List<HistoricoStatus> historico = new ArrayList<>();

    public OrdemServico() {}

    @PrePersist
    public void prePersist() {
        if (dataEntrada == null) {
            dataEntrada = LocalDateTime.now();
        }
    }

    public void adicionarItem(ItemOrdemServico item) {
        item.setOrdem(this);
        itens.add(item);
        recalcularTotal();
    }

    public void removerItem(ItemOrdemServico item) {
        itens.remove(item);
        item.setOrdem(null);
        recalcularTotal();
    }

    public void registrarHistorico(HistoricoStatus registro) {
        registro.setOrdem(this);
        historico.add(registro);
    }

    public void recalcularTotal() {
        this.valorTotal = itens.stream()
                .map(ItemOrdemServico::getSubtotal)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    /** Número exibido para o cliente, ex.: "OS 000123". */
    public String getNumero() {
        return id == null ? "" : String.format("OS %06d", id);
    }

    /** Percentual da barra de progresso (0 a 100). */
    public int getProgresso() {
        return status.getEtapa() * 20;
    }

    public boolean isAtrasada() {
        return previsaoEntrega != null
                && !status.isFinalizado()
                && status != StatusServico.PRONTO
                && previsaoEntrega.isBefore(LocalDate.now());
    }

    // Getters e Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Veiculo getVeiculo() { return veiculo; }
    public void setVeiculo(Veiculo veiculo) { this.veiculo = veiculo; }

    public Cliente getCliente() { return cliente; }
    public void setCliente(Cliente cliente) { this.cliente = cliente; }

    public StatusServico getStatus() { return status; }
    public void setStatus(StatusServico status) { this.status = status; }

    public String getDescricaoProblema() { return descricaoProblema; }
    public void setDescricaoProblema(String descricaoProblema) { this.descricaoProblema = descricaoProblema; }

    public String getObservacoes() { return observacoes; }
    public void setObservacoes(String observacoes) { this.observacoes = observacoes; }

    public Integer getQuilometragem() { return quilometragem; }
    public void setQuilometragem(Integer quilometragem) { this.quilometragem = quilometragem; }

    public LocalDateTime getDataEntrada() { return dataEntrada; }
    public void setDataEntrada(LocalDateTime dataEntrada) { this.dataEntrada = dataEntrada; }

    public LocalDate getPrevisaoEntrega() { return previsaoEntrega; }
    public void setPrevisaoEntrega(LocalDate previsaoEntrega) { this.previsaoEntrega = previsaoEntrega; }

    public LocalDateTime getDataConclusao() { return dataConclusao; }
    public void setDataConclusao(LocalDateTime dataConclusao) { this.dataConclusao = dataConclusao; }

    public LocalDateTime getDataEntrega() { return dataEntrega; }
    public void setDataEntrega(LocalDateTime dataEntrega) { this.dataEntrega = dataEntrega; }

    public BigDecimal getValorTotal() { return valorTotal; }

    public List<ItemOrdemServico> getItens() { return itens; }

    public List<HistoricoStatus> getHistorico() { return historico; }
}
