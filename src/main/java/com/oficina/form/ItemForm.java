package com.oficina.form;

import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;

/** Peça ou serviço avulso lançado numa ordem. */
public class ItemForm {

    @NotBlank(message = "Informe a descrição")
    @Size(max = 150, message = "Descrição muito longa")
    private String descricao;

    @Min(value = 1, message = "Quantidade mínima é 1")
    @Max(value = 999, message = "Quantidade muito alta")
    private int quantidade = 1;

    @NotNull(message = "Informe o valor")
    @DecimalMin(value = "0.00", message = "O valor não pode ser negativo")
    @DecimalMax(value = "999999.99", message = "Valor muito alto")
    @Digits(integer = 6, fraction = 2, message = "Use no máximo 2 casas decimais")
    private BigDecimal valorUnitario;

    public String getDescricao() { return descricao; }
    public void setDescricao(String descricao) { this.descricao = descricao; }

    public int getQuantidade() { return quantidade; }
    public void setQuantidade(int quantidade) { this.quantidade = quantidade; }

    public BigDecimal getValorUnitario() { return valorUnitario; }
    public void setValorUnitario(BigDecimal valorUnitario) { this.valorUnitario = valorUnitario; }
}
