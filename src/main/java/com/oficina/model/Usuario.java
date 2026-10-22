package com.oficina.model;

import jakarta.persistence.*;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.time.LocalDateTime;

/**
 * Quem pode entrar no sistema: administrador, recepção ou o dono do carro (cliente).
 * Para o cliente, o login é o CPF (só dígitos).
 */
@Entity
@Table(name = "usuarios")
public class Usuario {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(length = 60, unique = true, nullable = false)
    private String login;

    /** Senha criptografada (BCrypt). Nunca guardamos a senha em texto. */
    @Column(length = 100, nullable = false)
    private String senhaHash;

    @Column(length = 120, nullable = false)
    private String nome;

    @Enumerated(EnumType.STRING)
    @JdbcTypeCode(SqlTypes.VARCHAR)
    @Column(length = 20, nullable = false)
    private Perfil perfil;

    private boolean ativo = true;

    /** Preenchido só para usuários do tipo CLIENTE. */
    @OneToOne
    @JoinColumn(name = "cliente_id", unique = true)
    private Cliente cliente;

    @Column(updatable = false)
    private LocalDateTime dataCadastro;

    public Usuario() {}

    public Usuario(String login, String senhaHash, String nome, Perfil perfil) {
        this.login = login;
        this.senhaHash = senhaHash;
        this.nome = nome;
        this.perfil = perfil;
    }

    @PrePersist
    public void prePersist() {
        this.dataCadastro = LocalDateTime.now();
    }

    // Getters e Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getLogin() { return login; }
    public void setLogin(String login) { this.login = login; }

    public String getSenhaHash() { return senhaHash; }
    public void setSenhaHash(String senhaHash) { this.senhaHash = senhaHash; }

    public String getNome() { return nome; }
    public void setNome(String nome) { this.nome = nome; }

    public Perfil getPerfil() { return perfil; }
    public void setPerfil(Perfil perfil) { this.perfil = perfil; }

    public boolean isAtivo() { return ativo; }
    public void setAtivo(boolean ativo) { this.ativo = ativo; }

    public Cliente getCliente() { return cliente; }
    public void setCliente(Cliente cliente) { this.cliente = cliente; }

    public LocalDateTime getDataCadastro() { return dataCadastro; }
}
