package com.oficina.form;

import com.oficina.model.Veiculo;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public class VeiculoForm {

    private Long id;

    @NotNull(message = "Selecione o dono do veículo")
    private Long clienteId;

    @NotBlank(message = "Informe a placa")
    @Size(max = 10, message = "Placa inválida")
    private String placa;

    @NotBlank(message = "Informe a marca")
    @Size(max = 60, message = "Marca muito longa")
    private String marca;

    @NotBlank(message = "Informe o modelo")
    @Size(max = 60, message = "Modelo muito longo")
    private String modelo;

    @Min(value = 1900, message = "Ano inválido")
    @Max(value = 2100, message = "Ano inválido")
    private Integer ano;

    @Size(max = 30, message = "Cor muito longa")
    private String cor;

    public VeiculoForm() {}

    public static VeiculoForm de(Veiculo v) {
        VeiculoForm f = new VeiculoForm();
        f.id = v.getId();
        f.clienteId = v.getCliente().getId();
        f.placa = v.getPlacaFormatada();
        f.marca = v.getMarca();
        f.modelo = v.getModelo();
        f.ano = v.getAno();
        f.cor = v.getCor();
        return f;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Long getClienteId() { return clienteId; }
    public void setClienteId(Long clienteId) { this.clienteId = clienteId; }

    public String getPlaca() { return placa; }
    public void setPlaca(String placa) { this.placa = placa; }

    public String getMarca() { return marca; }
    public void setMarca(String marca) { this.marca = marca; }

    public String getModelo() { return modelo; }
    public void setModelo(String modelo) { this.modelo = modelo; }

    public Integer getAno() { return ano; }
    public void setAno(Integer ano) { this.ano = ano; }

    public String getCor() { return cor; }
    public void setCor(String cor) { this.cor = cor; }
}
