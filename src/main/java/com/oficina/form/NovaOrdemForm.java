package com.oficina.form;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/** Entrada de um carro na oficina. */
public class NovaOrdemForm {

    @NotNull(message = "Veículo não informado")
    private Long veiculoId;

    @Min(value = 0, message = "Quilometragem inválida")
    @Max(value = 9_999_999, message = "Quilometragem inválida")
    private Integer quilometragem;

    @Size(max = 2000, message = "Texto muito longo")
    private String descricaoProblema;

    @Size(max = 2000, message = "Texto muito longo")
    private String observacoes;

    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    private LocalDate previsaoEntrega;

    private List<Long> servicoIds = new ArrayList<>();

    public Long getVeiculoId() { return veiculoId; }
    public void setVeiculoId(Long veiculoId) { this.veiculoId = veiculoId; }

    public Integer getQuilometragem() { return quilometragem; }
    public void setQuilometragem(Integer quilometragem) { this.quilometragem = quilometragem; }

    public String getDescricaoProblema() { return descricaoProblema; }
    public void setDescricaoProblema(String descricaoProblema) { this.descricaoProblema = descricaoProblema; }

    public String getObservacoes() { return observacoes; }
    public void setObservacoes(String observacoes) { this.observacoes = observacoes; }

    public LocalDate getPrevisaoEntrega() { return previsaoEntrega; }
    public void setPrevisaoEntrega(LocalDate previsaoEntrega) { this.previsaoEntrega = previsaoEntrega; }

    public List<Long> getServicoIds() { return servicoIds; }
    public void setServicoIds(List<Long> servicoIds) { this.servicoIds = servicoIds; }
}
