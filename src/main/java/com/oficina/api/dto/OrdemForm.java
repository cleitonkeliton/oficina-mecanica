package com.oficina.api.dto;

import com.oficina.api.model.OrdemServico;
import com.oficina.api.model.Servico;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

// Dados do formulário de ordem de serviço da área administrativa
public class OrdemForm {

    private Long id;
    private Long veiculoId;
    private List<Long> servicoIds = new ArrayList<>();
    private String status = OrdemServico.STATUS.get(0);
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    private LocalDate previsaoEntrega;
    private String observacoes;

    public OrdemForm() {}

    // Preenche o formulário a partir de uma ordem existente (edição)
    public static OrdemForm de(OrdemServico ordem) {
        OrdemForm f = new OrdemForm();
        f.id = ordem.getId();
        if (ordem.getVeiculo() != null) {
            f.veiculoId = ordem.getVeiculo().getId();
        }
        if (ordem.getServicos() != null) {
            f.servicoIds = ordem.getServicos().stream().map(Servico::getId).toList();
        }
        f.status = ordem.getStatus();
        f.previsaoEntrega = ordem.getPrevisaoEntrega();
        f.observacoes = ordem.getObservacoes();
        return f;
    }

    // Getters e Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Long getVeiculoId() { return veiculoId; }
    public void setVeiculoId(Long veiculoId) { this.veiculoId = veiculoId; }

    public List<Long> getServicoIds() { return servicoIds; }
    public void setServicoIds(List<Long> servicoIds) { this.servicoIds = servicoIds; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public LocalDate getPrevisaoEntrega() { return previsaoEntrega; }
    public void setPrevisaoEntrega(LocalDate previsaoEntrega) { this.previsaoEntrega = previsaoEntrega; }

    public String getObservacoes() { return observacoes; }
    public void setObservacoes(String observacoes) { this.observacoes = observacoes; }
}
