package com.oficina.api.model;

import jakarta.persistence.*;

// Dados institucionais exibidos na página principal. Existe um único registro (id = 1).
@Entity
@Table(name = "informacoes_oficina")
public class InformacaoOficina {

    public static final Long ID_UNICO = 1L;

    @Id
    private Long id = ID_UNICO;

    private String nome;

    private String slogan;

    @Column(length = 2000)
    private String sobre;

    private String telefone;

    private String whatsapp;

    private String email;

    private String endereco;

    @Column(length = 500)
    private String horarioFuncionamento;

    public InformacaoOficina() {}

    // Getters e Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getNome() { return nome; }
    public void setNome(String nome) { this.nome = nome; }

    public String getSlogan() { return slogan; }
    public void setSlogan(String slogan) { this.slogan = slogan; }

    public String getSobre() { return sobre; }
    public void setSobre(String sobre) { this.sobre = sobre; }

    public String getTelefone() { return telefone; }
    public void setTelefone(String telefone) { this.telefone = telefone; }

    public String getWhatsapp() { return whatsapp; }
    public void setWhatsapp(String whatsapp) { this.whatsapp = whatsapp; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getEndereco() { return endereco; }
    public void setEndereco(String endereco) { this.endereco = endereco; }

    public String getHorarioFuncionamento() { return horarioFuncionamento; }
    public void setHorarioFuncionamento(String horarioFuncionamento) { this.horarioFuncionamento = horarioFuncionamento; }

    // Só os dígitos, para montar o link wa.me
    public String getWhatsappLink() {
        if (whatsapp == null) return null;
        String digitos = whatsapp.replaceAll("\\D", "");
        return digitos.isEmpty() ? null : "https://wa.me/" + digitos;
    }
}
