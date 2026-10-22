package com.oficina.form;

import com.oficina.model.Perfil;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

/** Cadastro de alguém da equipe (administrador ou recepção). */
public class UsuarioForm {

    @NotBlank(message = "Informe o nome")
    @Size(max = 120, message = "Nome muito longo")
    private String nome;

    @NotBlank(message = "Informe o login")
    @Size(min = 3, max = 60, message = "O login deve ter de 3 a 60 caracteres")
    private String login;

    @NotBlank(message = "Informe a senha")
    @Size(min = 6, max = 60, message = "A senha deve ter de 6 a 60 caracteres")
    private String senha;

    @NotNull(message = "Selecione o perfil")
    private Perfil perfil = Perfil.RECEPCAO;

    public String getNome() { return nome; }
    public void setNome(String nome) { this.nome = nome; }

    public String getLogin() { return login; }
    public void setLogin(String login) { this.login = login; }

    public String getSenha() { return senha; }
    public void setSenha(String senha) { this.senha = senha; }

    public Perfil getPerfil() { return perfil; }
    public void setPerfil(Perfil perfil) { this.perfil = perfil; }
}
