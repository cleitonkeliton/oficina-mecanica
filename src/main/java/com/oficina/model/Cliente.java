package com.oficina.model;

import com.oficina.util.Documentos;
import jakarta.persistence.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/** Proprietário do carro. */
@Entity
@Table(name = "clientes")
public class Cliente {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(length = 120)
    private String nome;

    /** Guardado só com os 11 dígitos. Também é o login do cliente na área de acompanhamento. */
    @Column(length = 11, unique = true)
    private String cpf;

    @Column(length = 30)
    private String telefone;

    @Column(length = 120)
    private String email;

    @Column(name = "data_cadastro", updatable = false)
    private LocalDateTime dataCadastro;

    @OneToMany(mappedBy = "cliente")
    @OrderBy("placa ASC")
    private List<Veiculo> veiculos = new ArrayList<>();

    // Construtor vazio necessário para JPA
    public Cliente() {}

    public Cliente(String nome, String cpf, String telefone) {
        this.nome = nome;
        this.cpf = cpf;
        this.telefone = telefone;
    }

    @PrePersist
    public void prePersist() {
        this.dataCadastro = LocalDateTime.now();
    }

    public String getCpfFormatado() {
        return Documentos.formatarCpf(cpf);
    }

    // Getters e Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getNome() { return nome; }
    public void setNome(String nome) { this.nome = nome; }

    public String getCpf() { return cpf; }
    public void setCpf(String cpf) { this.cpf = cpf; }

    public String getTelefone() { return telefone; }
    public void setTelefone(String telefone) { this.telefone = telefone; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public LocalDateTime getDataCadastro() { return dataCadastro; }

    public List<Veiculo> getVeiculos() { return veiculos; }
}
