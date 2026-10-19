package com.oficina.form;

import com.oficina.model.OrdemServico;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Size;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;

/** Dados da ordem que podem ser alterados depois da entrada. */
public class OrdemDadosForm {

    @Min(value = 0, message = "Quilometragem inválida")
    @Max(value = 9_999_999, message = "Quilometragem inválida")
    private Integer quilometragem;

    @Size(max = 2000, message = "Texto muito longo")
    private String descricaoProblema;

    @Size(max = 2000, message = "Texto muito longo")
    private String observacoes;

    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    private LocalDate previsaoEntrega;

    public static OrdemDadosForm de(OrdemServico o) {
        OrdemDadosForm f = new OrdemDadosForm();
        f.quilometragem = o.getQuilometragem();
        f.descricaoProblema = o.getDescricaoProblema();
        f.observacoes = o.getObservacoes();
        f.previsaoEntrega = o.getPrevisaoEntrega();
        return f;
    }

    public Integer getQuilometragem() { return quilometragem; }
    public void setQuilometragem(Integer quilometragem) { this.quilometragem = quilometragem; }

    public String getDescricaoProblema() { return descricaoProblema; }
    public void setDescricaoProblema(String descricaoProblema) { this.descricaoProblema = descricaoProblema; }

    public String getObservacoes() { return observacoes; }
    public void setObservacoes(String observacoes) { this.observacoes = observacoes; }

    public LocalDate getPrevisaoEntrega() { return previsaoEntrega; }
    public void setPrevisaoEntrega(LocalDate previsaoEntrega) { this.previsaoEntrega = previsaoEntrega; }
}
