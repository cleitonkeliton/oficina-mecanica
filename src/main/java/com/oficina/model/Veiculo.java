package com.oficina.model;

import com.oficina.util.Documentos;
import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "veiculos")
public class Veiculo {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /** Guardada em maiúsculas e sem traço (ABC1234 ou ABC1D23). */
    @Column(length = 7, unique = true, nullable = false)
    private String placa;

    @Column(length = 60)
    private String marca;

    @Column(length = 60)
    private String modelo;

    private Integer ano;

    @Column(length = 30)
    private String cor;

    @ManyToOne(optional = false)
    @JoinColumn(name = "cliente_id", nullable = false)
    private Cliente cliente;

    @Column(name = "data_cadastro", updatable = false)
    private LocalDateTime dataCadastro;

    // Construtor vazio (necessário para JPA)
    public Veiculo() {}

    public Veiculo(String placa, String marca, String modelo, Integer ano, String cor, Cliente cliente) {
        this.placa = placa;
        this.marca = marca;
        this.modelo = modelo;
        this.ano = ano;
        this.cor = cor;
        this.cliente = cliente;
    }

    @PrePersist
    public void prePersist() {
        this.dataCadastro = LocalDateTime.now();
    }

    public String getPlacaFormatada() {
        return Documentos.formatarPlaca(placa);
    }

    /** Ex.: "Chevrolet Onix 2020". */
    public String getDescricao() {
        StringBuilder sb = new StringBuilder();
        if (marca != null && !marca.isBlank()) sb.append(marca.trim()).append(' ');
        if (modelo != null && !modelo.isBlank()) sb.append(modelo.trim()).append(' ');
        if (ano != null) sb.append(ano);
        return sb.toString().trim();
    }

    // Getters e Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

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

    public Cliente getCliente() { return cliente; }
    public void setCliente(Cliente cliente) { this.cliente = cliente; }

    public LocalDateTime getDataCadastro() { return dataCadastro; }
}
