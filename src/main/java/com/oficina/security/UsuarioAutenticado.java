package com.oficina.security;

import com.oficina.model.Perfil;
import com.oficina.model.Usuario;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.List;

/** Dados do usuário logado guardados na sessão. */
public class UsuarioAutenticado implements UserDetails {

    private final Long id;
    private final String login;
    private final String senhaHash;
    private final String nome;
    private final Perfil perfil;
    private final Long clienteId;
    private final boolean ativo;

    public UsuarioAutenticado(Usuario usuario) {
        this.id = usuario.getId();
        this.login = usuario.getLogin();
        this.senhaHash = usuario.getSenhaHash();
        this.nome = usuario.getNome();
        this.perfil = usuario.getPerfil();
        this.clienteId = usuario.getCliente() != null ? usuario.getCliente().getId() : null;
        this.ativo = usuario.isAtivo();
    }

    public Long getId() { return id; }
    public String getNome() { return nome; }
    public Perfil getPerfil() { return perfil; }
    public Long getClienteId() { return clienteId; }

    /** Primeiro nome, para "Olá, Maria". */
    public String getPrimeiroNome() {
        String n = nome == null ? "" : nome.trim();
        int espaco = n.indexOf(' ');
        return espaco > 0 ? n.substring(0, espaco) : n;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of(new SimpleGrantedAuthority(perfil.getAuthority()));
    }

    @Override
    public String getPassword() { return senhaHash; }

    @Override
    public String getUsername() { return login; }

    @Override
    public boolean isEnabled() { return ativo; }
}
