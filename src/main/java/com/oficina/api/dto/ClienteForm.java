package com.oficina.api.dto;

import com.oficina.api.model.Cliente;

// Cadastro do cliente junto com o veículo, na área administrativa
public class ClienteForm {

    private Long id;
    private String nome;
    private String telefone;
    private String email;
    private String cpf;
    // Veículo cadastrado junto (só no cadastro novo)
    private String marca;
    private String modelo;
    private String placa;

    public ClienteForm() {}

    public static ClienteForm de(Cliente c) {
        ClienteForm f = new ClienteForm();
        f.id = c.getId();
        f.nome = c.getNome();
        f.telefone = c.getTelefone();
        f.email = c.getEmail();
        f.cpf = c.getCpf();
        return f;
    }

    // Getters e Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getNome() { return nome; }
    public void setNome(String nome) { this.nome = nome; }

    public String getTelefone() { return telefone; }
    public void setTelefone(String telefone) { this.telefone = telefone; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getCpf() { return cpf; }
    public void setCpf(String cpf) { this.cpf = cpf; }

    public String getMarca() { return marca; }
    public void setMarca(String marca) { this.marca = marca; }

    public String getModelo() { return modelo; }
    public void setModelo(String modelo) { this.modelo = modelo; }

    public String getPlaca() { return placa; }
    public void setPlaca(String placa) { this.placa = placa; }
}
