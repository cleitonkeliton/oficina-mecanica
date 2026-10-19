package com.oficina.api.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.oficina.util.Codigos;
import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "clientes")
public class Cliente {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String nome;

    private String telefone;

    private String email;

    private String cpf;

    @Column(name = "data_cadastro", updatable = false)
    private LocalDateTime dataCadastro;

    // Código que o cliente usa, junto com a placa, para entrar na área do cliente
    @JsonIgnore
    @Column(name = "codigo_acesso", unique = true, length = 12)
    private String codigoAcesso;

    // Construtor vazio necessário para JPA
    public Cliente() {}

    // Construtor para uso no DAO ou testes
    public Cliente(String nome, String cpf, String telefone) {
        this.nome = nome;
        this.cpf = cpf;
        this.telefone = telefone;
    }

    @PrePersist
    public void prePersist() {
        this.dataCadastro = LocalDateTime.now();
        if (this.codigoAcesso == null) {
            this.codigoAcesso = Codigos.gerar(8);
        }
    }

    // Clientes cadastrados antes do código existir, ou atualizados pela API (que não envia o código)
    @PreUpdate
    public void preUpdate() {
        if (this.codigoAcesso == null) {
            this.codigoAcesso = Codigos.gerar(8);
        }
    }

    // Getters e Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getTelefone() {
        return telefone;
    }

    public void setTelefone(String telefone) {
        this.telefone = telefone;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getCpf() {
        return cpf;
    }

    public void setCpf(String cpf) {
        this.cpf = cpf;
    }

    public LocalDateTime getDataCadastro() {
        return dataCadastro;
    }

    public String getCodigoAcesso() {
        return codigoAcesso;
    }

    public void setCodigoAcesso(String codigoAcesso) {
        this.codigoAcesso = codigoAcesso;
    }

    // Número no formato do wa.me; números de 10 ou 11 dígitos recebem o 55 do Brasil
    @JsonIgnore
    public String getWhatsappNumero() {
        if (telefone == null) return null;
        String digitos = telefone.replaceAll("\\D", "");
        if (digitos.length() == 10 || digitos.length() == 11) digitos = "55" + digitos;
        return digitos.length() < 12 ? null : digitos;
    }
}
